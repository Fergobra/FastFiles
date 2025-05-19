package com.example.fastfiles;

import android.content.Context;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileUploadServer extends NanoHTTPD {

    private final Context context;

    public FileUploadServer(Context context) {
        super(8080);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (Method.POST.equals(session.getMethod())) {
            try {
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String filename = session.getParms().get("filename");
                String tempFilePath = files.get("file");

                File dest = new File(context.getExternalFilesDir(null), filename);
                FileInputStream fis = new FileInputStream(tempFilePath);
                FileOutputStream fos = new FileOutputStream(dest);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();

                return Response.newFixedLengthResponse(Status.OK, "text/plain", "OK");
            } catch (Exception e) {
                return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, "text/plain", "Error: " + e.getMessage());
            }
        }

        String html = "<html><body>" +
                "<h2>Arrastra aquí tus archivos</h2>" +
                "<div id='dropZone' style='width:100%;height:300px;border:2px dashed gray;text-align:center;line-height:300px;'>Suelta aquí</div>" +
                "<script>" +
                "const dropZone = document.getElementById('dropZone');" +
                "dropZone.ondragover = e => e.preventDefault();" +
                "dropZone.ondrop = async e => {" +
                "  e.preventDefault();" +
                "  for (const file of e.dataTransfer.files) {" +
                "    const formData = new FormData();" +
                "    formData.append('file', file);" +
                "    const res = await fetch('/?filename=' + encodeURIComponent(file.name), {method:'POST', body:formData});" +
                "    const msg = await res.text();" +
                "    alert('Archivo subido: ' + file.name);" +
                "  }" +
                "};" +
                "</script></body></html>";

        return Response.newFixedLengthResponse(Status.OK, "text/html", html);
    }
}
