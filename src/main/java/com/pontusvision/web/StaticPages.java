package com.pontusvision.web;

import org.apache.commons.io.FileUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Paths;


@Path("pv")
public class StaticPages {

  //  @Inject
  //  KeycloakSecurityContext keycloakSecurityContext;


  public StaticPages() {

  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String staticPages() throws IOException {

    java.nio.file.Path resourceDirectory = Paths.get(".");
    String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
    String idxHtmlPathStr = Paths.get(pwdAbsolutePath, "config", "index.html").toString();

    String idxHtmlStr = FileUtils.readFileToString(new File(idxHtmlPathStr), "UTF-8");

    return idxHtmlStr;

  }


}
