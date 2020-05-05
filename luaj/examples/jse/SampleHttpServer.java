import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;

/**
 * Created by nirenr on 2019/11/8.
 */
public class SampleHttpServer {
    public static void main(String[] args) throws IOException {
        // create an environment to run in
        // Use the convenience function on Globals to load a chunk.
        int port=8080;
        int max=100;
        try{
            port= Integer.valueOf(args[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            max= Integer.valueOf(args[1]);
        } catch (Exception e){
            e.printStackTrace();
        }
        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(port), max);//监听端口8080,能同时接受100个请求
        httpserver.createContext("/", new LuaResponseHandler());
        httpserver.setExecutor(null);
        httpserver.start();

        System.out.println("server started port: " +port);
    }

    public static class LuaResponseHandler implements HttpHandler {
        private Globals globals = JsePlatform.standardGlobals();
        public LuaResponseHandler() {
            globals.load(new json());
            globals.load(new file());
            globals.jset("http",http.class);
            String script = "httpServer.lua";
            LuaValue chunk = globals.loadfile(script);
            chunk.call();
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("date: "+new Date());
            String requestMethod = httpExchange.getRequestMethod();
            //在这里通过httpExchange获取客户端发送过来的消息
            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath().substring(1);
            LuaValue func = globals.get(path);
            if (path.isEmpty()) {
                func = globals.get("main");
                //System.out.println("server path main");
            }
            System.out.println("url: " + httpExchange.getRemoteAddress());
            System.out.println("path: " + uri.getPath());
            if(!path.endsWith("lua")){
                File f = new File(path);
                if(f.exists()){
                    System.out.println("file: " + path);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    FileInputStream in = new FileInputStream(f);
                    byte[] buf = new byte[8196];
                    int l=0;
                    while ((l=in.read(buf))!=-1)
                        responseBody.write(buf,0,l);
                    in.close();
                    responseBody.close();
                    return;
                }
            }
            if (requestMethod.equalsIgnoreCase("GET")) {//客户端的请求是get方法
                //设置服务端响应的编码格式，否则在客户端收到的可能是乱码

                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/html;charset=utf-8");

                String response = "404 not found";
                System.out.println("query: " + uri.getQuery());
                if (func.isfunction()) {
                    response = func.jcall(uri.getQuery()).toString();
                }

                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes("UTF-8").length);

                System.out.println("resp: " + response);
                OutputStream responseBody = httpExchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(responseBody, "UTF-8");
                writer.write(response);
                writer.close();
                responseBody.close();
            }
            if (requestMethod.equalsIgnoreCase("POST")) {//客户端的请求是post方法
                //设置服务端响应的编码格式，否则在客户端收到的可能是乱码
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/html;charset=utf-8");

                //在这里通过httpExchange获取客户端发送过来的消息
                BufferedReader buff = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = buff.readLine()) != null) {
                    System.out.println("post: " + line);
                    buf.append(line).append("\n");
                }
                String response = "404 not found";

                if (func.isfunction()) {
                    response = func.jcall(buf.toString()).toString();
                }

                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes("UTF-8").length);

                System.out.println("resp: " + response);

                OutputStream responseBody = httpExchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(responseBody, "UTF-8");
                writer.write(response);
                writer.close();
                responseBody.close();
            }
        }
    }
}

