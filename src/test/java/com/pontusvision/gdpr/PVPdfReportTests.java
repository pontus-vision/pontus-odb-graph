package com.pontusvision.gdpr;

import com.pontusvision.gdpr.report.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test0000 for simple App.
 */
@TestClassOrder(AnnotationTestsOrderer.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassesOrder(2034)
public class PVPdfReportTests extends AppTest {

  public HttpResponse sendPdfReqPayload(String payload, int expectedHttpStatus) throws IOException {

    HttpClient client = HttpClients.createMinimal();

    HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/report/pdf/render"));
    request.setHeader("Content-Type", "application/json");
    request.setHeader("Accept", "application/json");
    PdfReportRenderRequest req = new PdfReportRenderRequest();
    req.setRefEntryId("123");
    String htmlRep = payload;

    req.setBase64Report(Base64.getEncoder().encodeToString(htmlRep.getBytes()));


    StringEntity data = new StringEntity(req.toJson());
    request.setEntity(data);

    HttpResponse resp = client.execute(request);
    assertEquals(expectedHttpStatus, resp.getStatusLine().getStatusCode(), "Expected Error code");
    return resp;
  }
  public ReportTemplateRenderResponse sendReportReqPayload(String contextId, int expectedHttpStatus) throws IOException {


    ReportTemplateRenderRequest reportReq = new ReportTemplateRenderRequest();
    reportReq.setTemplateId("Object_Data_Procedures/RIPD");
    reportReq.setRefEntryId(contextId);

    HttpClient client = HttpClients.createMinimal();
    HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/report/template/render"));
    request.setHeader("Content-Type", "application/json");
    request.setHeader("Accept", "application/json");
    StringEntity data = new StringEntity(gson.toJson(reportReq));
    request.setEntity(data);

    HttpResponse httpResponse = client.execute(request);
    assertEquals(expectedHttpStatus, httpResponse.getStatusLine().getStatusCode(), "Expected Error code");
    String output = IOUtils.toString(httpResponse.getEntity().getContent(),Charset.defaultCharset());
    return gson.fromJson(output,ReportTemplateRenderResponse.class);
  }

  /**
   * @return the suite of test0000s being test0000ed
   */
//  public static String helloWorldPdfB64 = "JVBERi0xLjQKJfbk/N8KMSAwIG9iago8PAovVHlwZSAvQ2F0YWxvZwovVmVyc2lvbiAvMS43Ci9QYWdlcyAyIDAgUgo+PgplbmRvYmoKMyAwIG9iago8PAovQ3JlYXRpb25EYXRlIChEOjIwMjIwNzMwMTUyNTAzKzAwJzAwJykKL1Byb2R1Y2VyIChvcGVuaHRtbHRvcGRmLmNvbSkKPj4KZW5kb2JqCjIgMCBvYmoKPDwKL1R5cGUgL1BhZ2VzCi9LaWRzIFs0IDAgUl0KL0NvdW50IDEKPj4KZW5kb2JqCjQgMCBvYmoKPDwKL1R5cGUgL1BhZ2UKL01lZGlhQm94IFswLjAgMC4wIDU5NS4yNzUgODQxLjg3NV0KL1BhcmVudCAyIDAgUgovQ29udGVudHMgNSAwIFIKL1Jlc291cmNlcyA2IDAgUgo+PgplbmRvYmoKNSAwIG9iago8PAovTGVuZ3RoIDEzOAovRmlsdGVyIC9GbGF0ZURlY29kZQo+PgpzdHJlYW0NCnicVY07C8IwHMT3+xQ36hL/SZpHV7EiEilKoIO4iUqxgi5+fVMHH9x0P+4hSmxwfMJwDWEPLdxgf6DwiDusZxSnYokMcK5WJjgxH3b9YdaPU1UssLT+3Td/QYdbORr1OGOeMVtqmor5BP2munQZfFDWm6pmHjBZNSm17NpdWkyZezQZW7wAfMMlxg0KZW5kc3RyZWFtCmVuZG9iago2IDAgb2JqCjw8Ci9Gb250IDcgMCBSCj4+CmVuZG9iago3IDAgb2JqCjw8Ci9GMSA4IDAgUgo+PgplbmRvYmoKOCAwIG9iago8PAovVHlwZSAvRm9udAovU3VidHlwZSAvVHlwZTEKL0Jhc2VGb250IC9UaW1lcy1Cb2xkCi9FbmNvZGluZyAvV2luQW5zaUVuY29kaW5nCj4+CmVuZG9iagp4cmVmCjAgOQowMDAwMDAwMDAwIDY1NTM1IGYNCjAwMDAwMDAwMTUgMDAwMDAgbg0KMDAwMDAwMDE2OSAwMDAwMCBuDQowMDAwMDAwMDc4IDAwMDAwIG4NCjAwMDAwMDAyMjYgMDAwMDAgbg0KMDAwMDAwMDM0MiAwMDAwMCBuDQowMDAwMDAwNTU0IDAwMDAwIG4NCjAwMDAwMDA1ODcgMDAwMDAgbg0KMDAwMDAwMDYxOCAwMDAwMCBuDQp0cmFpbGVyCjw8Ci9Sb290IDEgMCBSCi9JbmZvIDMgMCBSCi9JRCBbPDk3M0I5REZERDMwNjg4Q0JBRTAxNjRGNTdBMEU2ODZCPiA8OTczQjlERkREMzA2ODhDQkFFMDE2NEY1N0EwRTY4NkI+XQovU2l6ZSA5Cj4+CnN0YXJ0eHJlZgo3MTYKJSVFT0YK";
//   what about "InterruptedException" that are grayed out ?!?!
  @Test
  public void test00001HappyPath() throws IOException {


    String htmlRep = "<h1>HELLO WORLD</h1>";
    HttpResponse resp = sendPdfReqPayload(htmlRep,200);

    InputStream respStreamJson = resp.getEntity().getContent();
    String respJsonStr = IOUtils.toString(respStreamJson, Charset.defaultCharset());

    PdfReportRenderResponse respFromJson = gson.fromJson(respJsonStr, PdfReportRenderResponse.class);
    respStreamJson.close();

//    assertEquals(helloWorldPdfB64, respFromJson.getBase64Report(),"Check PDF file report in base64");

    try (FileOutputStream outputStream = new FileOutputStream("my.pdf")) {

      byte[] bytes = Base64.getDecoder().decode(respFromJson.getBase64Report());
      outputStream.write(bytes, 0, bytes.length);

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  @Test
  public void test00002Errors() throws IOException {

    sendPdfReqPayload("",200);
    sendPdfReqPayload("<aha", 200);

  }

  @Test
  public void test00003RIPDPdf() throws IOException, InterruptedException, ExecutionException {
    jsonTestUtil("budibase/bb-mapeamentos.json", "$.rows", "bb_mapeamento_de_processo");

    String procId =
        App.executor.eval("App.g.V().has('Object_Data_Procedures_Interested_Parties_Consulted','joao@mail.com').id().next().toString()").get().toString();

    ReportTemplateRenderResponse resp = sendReportReqPayload(procId,200);

    HttpResponse pdfHttpResp = sendPdfReqPayload(new String(Base64.getDecoder().decode(resp.getBase64Report())),200);
    InputStream respStreamJson = pdfHttpResp.getEntity().getContent();
    String respJsonStr = IOUtils.toString(respStreamJson, Charset.defaultCharset());
    respStreamJson.close();
    PdfReportRenderResponse respFromJson = gson.fromJson(respJsonStr, PdfReportRenderResponse.class);
    try (FileOutputStream outputStream = new FileOutputStream("ripd.pdf")) {

      byte[] bytes = Base64.getDecoder().decode(respFromJson.getBase64Report());
      outputStream.write(bytes, 0, bytes.length);

    } catch (IOException e) {
      e.printStackTrace();
      assertNull(e);
    }


  }



}
