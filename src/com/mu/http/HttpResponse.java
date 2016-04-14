package com.mu.http;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mu.util.Charset;

public class HttpResponse {
    private ResponseStatusCode code;
    private OutputStream outputStream;
    
    private static final String CONN_CLOSE_HEADER_STRING =
        new Header("Connection", "close").value();
    private Charset charset = Charset.UTF8;
    private ContentType contentType;
    private long contentLengthInBytes;

    public enum ContentType {
        TEXT("text/plain"),
        HTML("text/html"),
        CSS("text/css"),
        PNG("image/png"),
        PDF("application/pdf"),
        JS("text/javascript"),
        JSON("application/json");
        
        private String mimeType;
        
        private ContentType(String value) {
            this.mimeType = value;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
    
    public static final Map<String, ContentType> CONTENT_TYPE_BY_MIME_TYPE = 
        Arrays.asList(ContentType.values())
            .stream()
            .collect(Collectors.toMap(ct -> ct.getMimeType(), ct -> ct));
    
    public class ContentTypeHeader {
        private ContentType contentType;
        private Charset charset;
        
        public ContentTypeHeader(ContentType contentType, Charset charset) {
            this.contentType = contentType;
            this.charset = charset;
        }
        
        public Header getHeader() {
            return new Header("Content-Type", getHeaderValue());
        }
        
        public String getHeaderValue() {
            String value = contentType.getMimeType();
            if (charset != null) {
                value += "; charset=" + charset.getValue();
            }
            return value;
        }
    }
    
    public void setOuputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public void renderHTML(String html) {
        contentType = ContentType.HTML;
        renderText(html);
    }
    
    public void renderText(String text) {
        this.contentLengthInBytes = text.length();
        PrintStream pstream = writeHeaders();
        pstream.println(text);
        pstream.flush();
        pstream.close();        
    }
    
    public PrintStream writeHeaders() {
        PrintStream pstream = new PrintStream(outputStream);
        pstream.println(code.getResponseHeader());
        pstream.println(getContentTypeHeader().value());
        pstream.println(new Header("Content-Length", String.valueOf(contentLengthInBytes)).value());
        pstream.println(CONN_CLOSE_HEADER_STRING);
        pstream.println();
        pstream.flush();
        return pstream;
    }
    
    private Header getContentTypeHeader() {
        return new ContentTypeHeader(contentType, charset).getHeader();
    }
    
    public void setContentLength(long contentLengthInBytes) {
        this.contentLengthInBytes = contentLengthInBytes;
    }
    
    public void setResponseStatusCode(ResponseStatusCode code) {
        this.code = code;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}