package au.com.totemsoft.amax.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

public class TextHttpInputMessage implements HttpInputMessage {

    private final byte[] body;

    private HttpHeaders headers;

    public TextHttpInputMessage(byte[] body) {
        this.body = body;
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(body);
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
