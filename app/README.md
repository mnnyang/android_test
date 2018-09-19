## OkHttp同步异步方法
1. 创建OkHttpClient和Request对象
2. 将Request封装成Call对象
3. 调用Call的execute()发送同步请求/call.enqueue异步请求

**注意**：

- 同步请求将会阻塞直到收到HTTP响应。
- 异步请求的new Callback()的**onFailure和onResponse是在子线程执行**。

**同步**

直接把call丢给了Dispatcher的executed方法（它直接把call丢到了runningSyncCalls"同步请求队列"中）。

同步请求执行完毕之后，在finally中调用client.dispatcher().finished()把call移除同步请求队列。

**异步**

call封装成AsyncCall(实现了Runnable)，然后丢给Dispatcher的enqueue方法。

enqueue判断请求数未达到设定的最大值时，把call添加到异步请求队列(runningAsyncCalls)，然后调用执行。否则把call添加到readyAsyncCalls(就绪队列)。



## 任务调度

发送的同步/异步请求都会在dispatcher中管理其状态。

**Dispatcher**

作用为维护请求的状态，并维护一个线程池，请求队列，用于执行请求。

```java
public final class Dispatcher {
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private @Nullable Runnable idleCallback;

    /** Executes calls. Created lazily. */
    private @Nullable ExecutorService executorService;

    /** Ready async calls in the order they'll be run. */
    private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();
```

使用就绪队列，这相当于Dispatcher在生产call，executorService在消费call。

**同步**

直接把call添加到了同步队列中。

**异步**

判断队列当前最大值，把call添加到相应的队列（ready/running），添加到running队列的，则调用线程池执行。

**ThreadPoolExecutor**

```java
if (executorService == null) {
  executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
}
```

- 0，60：一分钟后为使用，关闭所有无用线程。

- Integer.MAX_VALUE：线程扩充的最大值，但是这里受到maxRequests的控制。

线程执行完毕之后，也要调用dispatcher.finished()方法，把call移除队列。

**dispatcher.finished()**

```java
private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
  int runningCallsCount;
  Runnable idleCallback;
  synchronized (this) {
    if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
    if (promoteCalls) promoteCalls();
    runningCallsCount = runningCallsCount();
    idleCallback = this.idleCallback;
  }

  if (runningCallsCount == 0 && idleCallback != null) {
    idleCallback.run();
  }
}
```

1. calls.remove()把call移除队列

2. promoteCalls()调整队列

   **遍历等待队列，在最大请求的范围内，把等待队列中最大优先级的call调入运行队列。**

3. runningCallsCount()计算队列(同步+异步)长度



## 拦截器

它可以实现网络监听，请求以及响应重写、请求失败重试等功能。

在执行请求的方法中（同步）：

```java
@Override public Response execute() throws IOException {
    synchronized (this) {
        if (executed) throw new IllegalStateException("Already Executed");
        executed = true;
    }
    captureCallStackTrace();
    eventListener.callStart(this);
    try {
        client.dispatcher().executed(this);
        Response result = getResponseWithInterceptorChain();
        if (result == null) throw new IOException("Canceled");
        return result;
    } catch (IOException e) {
        eventListener.callFailed(this, e);
        throw e;
    } finally {
        client.dispatcher().finished(this);
    }
}
```

其实无论同步还是异步，都调用了` Response result = getResponseWithInterceptorChain();`

**它通过执行不同的拦截器获取服务器的响应返回。**

```java
Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
        interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,                                        
	...
    return chain.proceed(originalRequest);
}
```

1. 创建一系列拦截器，并将其放入一个拦截器List
2. 创建一个拦截器链RealLinterceptorChain，并执行他的proceed方法。

proceed()

```java
...
// Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec,
        connection, index + 1, request, call, eventListener, connectTimeout, readTimeout,
        writeTimeout);
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);
...
```

index+1，并且调用了`interceptor.intercept(next);`

拦截器的`intercept()`

```java
@Override public Response intercept(Chain chain) throws IOException {
    ...
        Response networkResponse = 			chain.proceed(requestBuilder.build());
    ...
```

继续调用链的proceed方法，继续下一个拦截器`intercept()`的执行。

总结：

1. 请求前对request进行处理。
2. 调用下一个拦截器，获取response
3. 对response处理，返回给上一个拦截器



### RetryAndFollowUpInterceptor

1. 创建StreamAllocation对象（网络请求需要的所有组件）。
2. 调用ReallntercaptorChain.proceed()进行网络请求。
3. **根据异常结果或响应结果判断是否要进行重新请求。**
4. 调用下一个拦截器，对response进行处理，返回给上一个拦截器。



### BridgeInterceptor

```java
	...
    if (body != null) {
      MediaType contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }
    ...
    Response networkResponse = chain.proceed(requestBuilder.build());

    HttpHeaders.receiveHeaders(cookieJar, userRequest.url(), networkResponse.headers());

    Response.Builder responseBuilder = networkResponse.newBuilder()
        .request(userRequest);
    if (transparentGzip
        && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding"))
        && HttpHeaders.hasBody(networkResponse)) {
        GzipSource responseBody = new GzipSource(networkResponse.body().source());
        Headers strippedHeaders = networkResponse.headers().newBuilder()
            .removeAll("Content-Encoding")
            .removeAll("Content-Length")
            .build();
        responseBuilder.headers(strippedHeaders);
        String contentType = networkResponse.header("Content-Type");
        responseBuilder.body(new RealResponseBody(contentType, -1L, Okio.buffer(responseBody)));
    }
```

1. 为Request添加一下请求头。转换为可以进行网络访问的请求。
2. 调用chain.proceed，将这个符合网络请求的Request进行网路请求。
3. 将网络请求回来的响应Response转换为用户可用的Response。(对支持gzip压缩的response解压)



### Cache

使用：

```java
private OkHttpClient client = new OkHttpClient
            .Builder()
            .cache(new Cache(new File("cache"), 20 * 1024 * 1024))
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

```

Cahe-put

```java
@Nullable CacheRequest put(Response response) {
    String requestMethod = response.request().method();

    if (HttpMethod.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      } catch (IOException ignored) {
      }
      return null;
    }
    if (!requestMethod.equals("GET")) {
      return null;
    }

    if (HttpHeaders.hasVaryAll(response)) {
      return null;
    }

    Entry entry = new Entry(response);
    DiskLruCache.Editor editor = null;
    try {
      editor = cache.edit(key(response.request().url()));
      if (editor == null) {
        return null;
      }
      entry.writeTo(editor);
      return new CacheRequestImpl(editor);
    } catch (IOException e) {
      abortQuietly(editor);
      return null;
    }
  }
```

- 对响应进行缓存。
- 只缓存GET的请求。
- 即缓存响应的，也缓存对应的请求的。

最终还是使用`DiskLruCache`进行写入缓存。

`CacheRequestImpl`是为了给缓存拦截器使用。

Cache-get

```java
...
try {
      entry = new Entry(snapshot.getSource(ENTRY_METADATA));
    } catch (IOException e) {
      Util.closeQuietly(snapshot);
      return null;
    }

    Response response = entry.response(snapshot);

    if (!entry.matches(request, response)) {
      Util.closeQuietly(response.body());
      return null;
    }

	 return response;
```

- 存在key的缓存，获取出来，包装成Entry再解析为Response，最后判断Request和response是否匹配，最后返回Response。



### CacheInterceptor



###　ConnectInterceptor

```java
@Override public Response intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    Request request = realChain.request();
    StreamAllocation streamAllocation = realChain.streamAllocation();

    // We need the network to satisfy this request. Possibly for validating a conditional GET.
    boolean doExtensiveHealthChecks = !request.method().equals("GET");
    HttpCodec httpCodec = streamAllocation.newStream(client, chain, doExtensiveHealthChecks);
    RealConnection connection = streamAllocation.connection();

    return realChain.proceed(request, streamAllocation, httpCodec, connection);
}
```

HttpCodec httpCodec = streamAllocation.newStream(client, chain, doExtensiveHealthChecks); 编码Request，解码response。

RealConnection connection 进行实际的网路io传输。

1. 获取Interceptor传过来的StreamAllocation

   创建或复用一个RealConnection对象，选择不同的链接方式。

2. 将刚才创建的用于网络IO的RealConnection对象及其httpCodec传递给后面的拦截器。



### CallServerInterceptor

1. 把http请求写到网络流中。

2. 从网络IO流中读取response。



### ConnectionPool

1. 每次http请求都会在拦截器中创建一个StreamAllocation对象。
2. StreamAllocation对象的弱引用添加到RealConnection对象的allocations集合。

3. StreamAllocation变为0，被线程池检测到并回收。



