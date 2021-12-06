package com.pontusvision.web;

import com.hubspot.jinjava.Jinjava;
import org.apache.commons.io.FileUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@Path("pv")
public class StaticPages {

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;
  public static  Jinjava j2 = new Jinjava();


  public StaticPages() {

  }

  @GET
  @Path("silent_keycloak_sso_iframe")
  @Produces(MediaType.TEXT_HTML)
  public String silentKeycloakSSOIframe() {
    return
        "<html>\n" +
            "<body>\n" +
            "    <script>\n" +
            "        parent.postMessage(location.href, location.origin)\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n!";
  }



  @GET
  @Produces(MediaType.TEXT_HTML)
  public String staticPages(@QueryParam("iframe") String iframe) throws IOException {

    java.nio.file.Path resourceDirectory = Paths.get(".");
    String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
    String idxHtmlPathStr = Paths.get(pwdAbsolutePath, "config", "index.html").toString();

    String idxHtmlStr = FileUtils.readFileToString(new File(idxHtmlPathStr), "UTF-8");
    Map<String,String> allData = new HashMap<>();
    allData.put("iframe", iframe);
    idxHtmlStr = j2.render(idxHtmlStr,allData);

    return idxHtmlStr;

  }



}
