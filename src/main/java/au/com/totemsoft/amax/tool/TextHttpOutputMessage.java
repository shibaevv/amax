package au.com.totemsoft.amax.tool;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

public class TextHttpOutputMessage implements HttpOutputMessage {

    /** replace MediaType.APPLICATION_JSON_UTF8 - using old spring-web 3.x */
    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

    private final OutputStream outputStream;

    private HttpHeaders headers;

    public TextHttpOutputMessage(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public OutputStream getBody() throws IOException {
        return outputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            //this.headers.add("Accept", "application/json");
        }
        return this.headers;
    }

}
