package com.pontusvision.gdpr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.apache.commons.csv.CSVFormat;
import com.orientechnologies.apache.commons.csv.CSVRecord;
import com.pontusvision.ingestion.Ingestion;
import com.pontusvision.ingestion.IngestionJsonObjArrayRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for simple App.
 */
public class AppTest {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */

  /**
   * @return the suite of tests being tested
   */

  public static Server server;

  public static String jsonReq = "{\n" +
      "    \"search\": {\n" +
      "        \"searchStr\": \"\",\n" +
      "        \"searchExact\": true,\n" +
      "        \"cols\": [{\n" +
      "                \"field\": \"Event_Data_Breach_Description\",\n" +
      "                \"id\": \"Event.Data_Breach.Description\",\n" +
      "                \"name\": \"Description\",\n" +
      "                \"sortable\": false,\n" +
      "                \"headerName\": \"Descrição\",\n" +
      "                \"filter\": false\n" +
      "            }, {\n" +
      "                \"field\": \"Event_Data_Breach_Impact\",\n" +
      "                \"id\": \"Event.Data_Breach.Impact\",\n" +
      "                \"name\": \"Impact\",\n" +
      "                \"sortable\": true,\n" +
      "                \"headerName\": \"Impacto\",\n" +
      "                \"filter\": true\n" +
      "            }, {\n" +
      "                \"field\": \"@[Data Breach]@[PGRpdiBzdHlsZT0ncGFkZGluZzogMTBweDsgYmFja2dyb3VuZDogYmxhY2snPgo8aHIvPgoKPGgxPiBTdW1tYXJ5IG9mIERhdGEgQnJlYWNoIEltcGFjdCA8L2gxPgo8aHIvPgogIFBlb3BsZToge3sgaW1wYWN0ZWRfcGVvcGxlIHwgbGVuZ3RoIH19PGJyLz4KICBEYXRhIFNvdXJjZXM6IHt7IGltcGFjdGVkX2RhdGFfc291cmNlcyB8IGxlbmd0aCB9fTxici8+CiAgU2VydmVycyA6IHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX08YnIvPgoKPGhyLz4KPGhyLz4KCgp7JSBpZiBpbXBhY3RlZF9wZW9wbGVbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9wZW9wbGUgfCBsZW5ndGggfX0gUGVvcGxlIGltcGFjdGVkIDwvaDI+CiAgPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+TmFtZTwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+RGF0ZSBPZiBCaXJ0aDwvdGg+PC90cj4KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9wZW9wbGUgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lJ10gLCBtYWlua2V5WydQZXJzb25fTmF0dXJhbF9EYXRlX09mX0JpcnRoJ10gKX19CiAgeyUgZW5kZm9yICV9CiAgICB7eyAiPC90YWJsZT4iIH19Cgp7JSBlbHNlICV9CiAgPGgyPiBObyBwZW9wbGUgaW1wYWN0ZWQgPC9oMj4KeyUgZW5kaWYgJX0KCgp7JSBpZiBpbXBhY3RlZF9kYXRhX3NvdXJjZXNbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgfCBsZW5ndGggfX0gRGF0YSBTb3VyY2VzIGltcGFjdGVkIDwvaDI+CiAge3sgIjx0YWJsZSBzdHlsZT0nbWFyZ2luOiA1cHgnPjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPk5hbWU8L3RoPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPlVwZGF0ZSBEYXRlPC90aD48L3RyPiIgfX0KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnT2JqZWN0X0RhdGFfU291cmNlX05hbWUnXSAsIG1haW5rZXlbJ09iamVjdF9EYXRhX1NvdXJjZV9VcGRhdGVfRGF0ZSddICl9fQogIHslIGVuZGZvciAlfQogICAge3sgIjwvdGFibGU+IiB9fQoKeyUgZWxzZSAlfQogIDxoMj4gTm8gZGF0YSBzb3VyY2VzIGltcGFjdGVkIDwvaDI+CnslIGVuZGlmICV9CiAgICAKeyUgaWYgaW1wYWN0ZWRfc2VydmVyc1swXSBpcyBkZWZpbmVkICV9CiAgPGgyPiBMaXN0IG9mIHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX0gU2VydmVycyBpbXBhY3RlZCA8L2gyPgoKIHslIGZvciBtYWlua2V5IGluIGltcGFjdGVkX3NlcnZlcnMgJX0KICA8aDM+e3sgbWFpbmtleS5NZXRhZGF0YV9UeXBlIHxyZXBsYWNlKCcuJywnICcpfHJlcGxhY2UoJ18nLCcgJykgfX08L2gzPgogIHt7ICI8dGFibGUgc3R5bGU9J21hcmdpbjogNXB4Jz48dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5OYW1lPC90aD48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5WYWx1ZTwvdGg+PC90cj4iIH19CiAgeyUgZm9yIGtleSwgdmFsdWUgaW4gbWFpbmtleS5pdGVtcygpICV9CiAge3sgICI8dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGtleSAsIHZhbHVlICl9fQogIHslIGVuZGZvciAlfQogIHt7ICI8L3RhYmxlPiIgfX0KICB7JSBlbmRmb3IgJX0KeyUgZWxzZSAlfQogIDxoMj4gTm8gU2VydmVycyBpbXBhY3RlZCA8L2gyPgp7JSBlbmRpZiAlfSAgICAKPC9kaXY+]\",\n" +
      "                \"id\": \"@[Data Breach]@[PGRpdiBzdHlsZT0ncGFkZGluZzogMTBweDsgYmFja2dyb3VuZDogYmxhY2snPgo8aHIvPgoKPGgxPiBTdW1tYXJ5IG9mIERhdGEgQnJlYWNoIEltcGFjdCA8L2gxPgo8aHIvPgogIFBlb3BsZToge3sgaW1wYWN0ZWRfcGVvcGxlIHwgbGVuZ3RoIH19PGJyLz4KICBEYXRhIFNvdXJjZXM6IHt7IGltcGFjdGVkX2RhdGFfc291cmNlcyB8IGxlbmd0aCB9fTxici8+CiAgU2VydmVycyA6IHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX08YnIvPgoKPGhyLz4KPGhyLz4KCgp7JSBpZiBpbXBhY3RlZF9wZW9wbGVbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9wZW9wbGUgfCBsZW5ndGggfX0gUGVvcGxlIGltcGFjdGVkIDwvaDI+CiAgPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+TmFtZTwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+RGF0ZSBPZiBCaXJ0aDwvdGg+PC90cj4KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9wZW9wbGUgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lJ10gLCBtYWlua2V5WydQZXJzb25fTmF0dXJhbF9EYXRlX09mX0JpcnRoJ10gKX19CiAgeyUgZW5kZm9yICV9CiAgICB7eyAiPC90YWJsZT4iIH19Cgp7JSBlbHNlICV9CiAgPGgyPiBObyBwZW9wbGUgaW1wYWN0ZWQgPC9oMj4KeyUgZW5kaWYgJX0KCgp7JSBpZiBpbXBhY3RlZF9kYXRhX3NvdXJjZXNbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgfCBsZW5ndGggfX0gRGF0YSBTb3VyY2VzIGltcGFjdGVkIDwvaDI+CiAge3sgIjx0YWJsZSBzdHlsZT0nbWFyZ2luOiA1cHgnPjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPk5hbWU8L3RoPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPlVwZGF0ZSBEYXRlPC90aD48L3RyPiIgfX0KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnT2JqZWN0X0RhdGFfU291cmNlX05hbWUnXSAsIG1haW5rZXlbJ09iamVjdF9EYXRhX1NvdXJjZV9VcGRhdGVfRGF0ZSddICl9fQogIHslIGVuZGZvciAlfQogICAge3sgIjwvdGFibGU+IiB9fQoKeyUgZWxzZSAlfQogIDxoMj4gTm8gZGF0YSBzb3VyY2VzIGltcGFjdGVkIDwvaDI+CnslIGVuZGlmICV9CiAgICAKeyUgaWYgaW1wYWN0ZWRfc2VydmVyc1swXSBpcyBkZWZpbmVkICV9CiAgPGgyPiBMaXN0IG9mIHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX0gU2VydmVycyBpbXBhY3RlZCA8L2gyPgoKIHslIGZvciBtYWlua2V5IGluIGltcGFjdGVkX3NlcnZlcnMgJX0KICA8aDM+e3sgbWFpbmtleS5NZXRhZGF0YV9UeXBlIHxyZXBsYWNlKCcuJywnICcpfHJlcGxhY2UoJ18nLCcgJykgfX08L2gzPgogIHt7ICI8dGFibGUgc3R5bGU9J21hcmdpbjogNXB4Jz48dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5OYW1lPC90aD48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5WYWx1ZTwvdGg+PC90cj4iIH19CiAgeyUgZm9yIGtleSwgdmFsdWUgaW4gbWFpbmtleS5pdGVtcygpICV9CiAge3sgICI8dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGtleSAsIHZhbHVlICl9fQogIHslIGVuZGZvciAlfQogIHt7ICI8L3RhYmxlPiIgfX0KICB7JSBlbmRmb3IgJX0KeyUgZWxzZSAlfQogIDxoMj4gTm8gU2VydmVycyBpbXBhY3RlZCA8L2gyPgp7JSBlbmRpZiAlfSAgICAKPC9kaXY+]\",\n" +
      "                \"name\": \"[Data Breach]\",\n" +
      "                \"sortable\": false,\n" +
      "                \"headerName\": \"[Data Breach]\",\n" +
      "                \"filter\": false\n" +
      "            }\n" +
      "        ],\n" +
      "        \"extraSearch\": {\n" +
      "            \"label\": \"Event.Data_Breach\",\n" +
      "            \"value\": \"Event.Data_Breach\"\n" +
      "        }\n" +
      "    },\n" +
      "    \"cols\": [{\n" +
      "            \"field\": \"Event_Data_Breach_Description\",\n" +
      "            \"id\": \"Event.Data_Breach.Description\",\n" +
      "            \"name\": \"Description\",\n" +
      "            \"sortable\": false,\n" +
      "            \"headerName\": \"Descrição\",\n" +
      "            \"filter\": false\n" +
      "        }, {\n" +
      "            \"field\": \"Event_Data_Breach_Impact\",\n" +
      "            \"id\": \"Event.Data_Breach.Impact\",\n" +
      "            \"name\": \"Impact\",\n" +
      "            \"sortable\": true,\n" +
      "            \"headerName\": \"Impacto\",\n" +
      "            \"filter\": true\n" +
      "        }, {\n" +
      "            \"field\": \"@[Data Breach]@[PGRpdiBzdHlsZT0ncGFkZGluZzogMTBweDsgYmFja2dyb3VuZDogYmxhY2snPgo8aHIvPgoKPGgxPiBTdW1tYXJ5IG9mIERhdGEgQnJlYWNoIEltcGFjdCA8L2gxPgo8aHIvPgogIFBlb3BsZToge3sgaW1wYWN0ZWRfcGVvcGxlIHwgbGVuZ3RoIH19PGJyLz4KICBEYXRhIFNvdXJjZXM6IHt7IGltcGFjdGVkX2RhdGFfc291cmNlcyB8IGxlbmd0aCB9fTxici8+CiAgU2VydmVycyA6IHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX08YnIvPgoKPGhyLz4KPGhyLz4KCgp7JSBpZiBpbXBhY3RlZF9wZW9wbGVbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9wZW9wbGUgfCBsZW5ndGggfX0gUGVvcGxlIGltcGFjdGVkIDwvaDI+CiAgPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+TmFtZTwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+RGF0ZSBPZiBCaXJ0aDwvdGg+PC90cj4KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9wZW9wbGUgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lJ10gLCBtYWlua2V5WydQZXJzb25fTmF0dXJhbF9EYXRlX09mX0JpcnRoJ10gKX19CiAgeyUgZW5kZm9yICV9CiAgICB7eyAiPC90YWJsZT4iIH19Cgp7JSBlbHNlICV9CiAgPGgyPiBObyBwZW9wbGUgaW1wYWN0ZWQgPC9oMj4KeyUgZW5kaWYgJX0KCgp7JSBpZiBpbXBhY3RlZF9kYXRhX3NvdXJjZXNbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgfCBsZW5ndGggfX0gRGF0YSBTb3VyY2VzIGltcGFjdGVkIDwvaDI+CiAge3sgIjx0YWJsZSBzdHlsZT0nbWFyZ2luOiA1cHgnPjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPk5hbWU8L3RoPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPlVwZGF0ZSBEYXRlPC90aD48L3RyPiIgfX0KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnT2JqZWN0X0RhdGFfU291cmNlX05hbWUnXSAsIG1haW5rZXlbJ09iamVjdF9EYXRhX1NvdXJjZV9VcGRhdGVfRGF0ZSddICl9fQogIHslIGVuZGZvciAlfQogICAge3sgIjwvdGFibGU+IiB9fQoKeyUgZWxzZSAlfQogIDxoMj4gTm8gZGF0YSBzb3VyY2VzIGltcGFjdGVkIDwvaDI+CnslIGVuZGlmICV9CiAgICAKeyUgaWYgaW1wYWN0ZWRfc2VydmVyc1swXSBpcyBkZWZpbmVkICV9CiAgPGgyPiBMaXN0IG9mIHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX0gU2VydmVycyBpbXBhY3RlZCA8L2gyPgoKIHslIGZvciBtYWlua2V5IGluIGltcGFjdGVkX3NlcnZlcnMgJX0KICA8aDM+e3sgbWFpbmtleS5NZXRhZGF0YV9UeXBlIHxyZXBsYWNlKCcuJywnICcpfHJlcGxhY2UoJ18nLCcgJykgfX08L2gzPgogIHt7ICI8dGFibGUgc3R5bGU9J21hcmdpbjogNXB4Jz48dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5OYW1lPC90aD48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5WYWx1ZTwvdGg+PC90cj4iIH19CiAgeyUgZm9yIGtleSwgdmFsdWUgaW4gbWFpbmtleS5pdGVtcygpICV9CiAge3sgICI8dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGtleSAsIHZhbHVlICl9fQogIHslIGVuZGZvciAlfQogIHt7ICI8L3RhYmxlPiIgfX0KICB7JSBlbmRmb3IgJX0KeyUgZWxzZSAlfQogIDxoMj4gTm8gU2VydmVycyBpbXBhY3RlZCA8L2gyPgp7JSBlbmRpZiAlfSAgICAKPC9kaXY+]\",\n" +
      "            \"id\": \"@[Data Breach]@[PGRpdiBzdHlsZT0ncGFkZGluZzogMTBweDsgYmFja2dyb3VuZDogYmxhY2snPgo8aHIvPgoKPGgxPiBTdW1tYXJ5IG9mIERhdGEgQnJlYWNoIEltcGFjdCA8L2gxPgo8aHIvPgogIFBlb3BsZToge3sgaW1wYWN0ZWRfcGVvcGxlIHwgbGVuZ3RoIH19PGJyLz4KICBEYXRhIFNvdXJjZXM6IHt7IGltcGFjdGVkX2RhdGFfc291cmNlcyB8IGxlbmd0aCB9fTxici8+CiAgU2VydmVycyA6IHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX08YnIvPgoKPGhyLz4KPGhyLz4KCgp7JSBpZiBpbXBhY3RlZF9wZW9wbGVbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9wZW9wbGUgfCBsZW5ndGggfX0gUGVvcGxlIGltcGFjdGVkIDwvaDI+CiAgPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+TmFtZTwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+RGF0ZSBPZiBCaXJ0aDwvdGg+PC90cj4KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9wZW9wbGUgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lJ10gLCBtYWlua2V5WydQZXJzb25fTmF0dXJhbF9EYXRlX09mX0JpcnRoJ10gKX19CiAgeyUgZW5kZm9yICV9CiAgICB7eyAiPC90YWJsZT4iIH19Cgp7JSBlbHNlICV9CiAgPGgyPiBObyBwZW9wbGUgaW1wYWN0ZWQgPC9oMj4KeyUgZW5kaWYgJX0KCgp7JSBpZiBpbXBhY3RlZF9kYXRhX3NvdXJjZXNbMF0gaXMgZGVmaW5lZCAlfQogIDxoMj4gTGlzdCBvZiB7eyBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgfCBsZW5ndGggfX0gRGF0YSBTb3VyY2VzIGltcGFjdGVkIDwvaDI+CiAge3sgIjx0YWJsZSBzdHlsZT0nbWFyZ2luOiA1cHgnPjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPk5hbWU8L3RoPjx0aCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPlVwZGF0ZSBEYXRlPC90aD48L3RyPiIgfX0KCiB7JSBmb3IgbWFpbmtleSBpbiBpbXBhY3RlZF9kYXRhX3NvdXJjZXMgJX0KICB7eyAgIjx0ciBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiVzPC90ZD48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+IiB8IGZvcm1hdCAobWFpbmtleVsnT2JqZWN0X0RhdGFfU291cmNlX05hbWUnXSAsIG1haW5rZXlbJ09iamVjdF9EYXRhX1NvdXJjZV9VcGRhdGVfRGF0ZSddICl9fQogIHslIGVuZGZvciAlfQogICAge3sgIjwvdGFibGU+IiB9fQoKeyUgZWxzZSAlfQogIDxoMj4gTm8gZGF0YSBzb3VyY2VzIGltcGFjdGVkIDwvaDI+CnslIGVuZGlmICV9CiAgICAKeyUgaWYgaW1wYWN0ZWRfc2VydmVyc1swXSBpcyBkZWZpbmVkICV9CiAgPGgyPiBMaXN0IG9mIHt7IGltcGFjdGVkX3NlcnZlcnMgfCBsZW5ndGggfX0gU2VydmVycyBpbXBhY3RlZCA8L2gyPgoKIHslIGZvciBtYWlua2V5IGluIGltcGFjdGVkX3NlcnZlcnMgJX0KICA8aDM+e3sgbWFpbmtleS5NZXRhZGF0YV9UeXBlIHxyZXBsYWNlKCcuJywnICcpfHJlcGxhY2UoJ18nLCcgJykgfX08L2gzPgogIHt7ICI8dGFibGUgc3R5bGU9J21hcmdpbjogNXB4Jz48dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5OYW1lPC90aD48dGggc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz5WYWx1ZTwvdGg+PC90cj4iIH19CiAgeyUgZm9yIGtleSwgdmFsdWUgaW4gbWFpbmtleS5pdGVtcygpICV9CiAge3sgICI8dHIgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz48dGQgc3R5bGU9J2JvcmRlcjogMXB4IHNvbGlkICNkZGRkZGQ7dGV4dC1hbGlnbjogbGVmdDtwYWRkaW5nOiA4cHg7Jz4lczwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGtleSAsIHZhbHVlICl9fQogIHslIGVuZGZvciAlfQogIHt7ICI8L3RhYmxlPiIgfX0KICB7JSBlbmRmb3IgJX0KeyUgZWxzZSAlfQogIDxoMj4gTm8gU2VydmVycyBpbXBhY3RlZCA8L2gyPgp7JSBlbmRpZiAlfSAgICAKPC9kaXY+]\",\n" +
      "            \"name\": \"[Data Breach]\",\n" +
      "            \"sortable\": false,\n" +
      "            \"headerName\": \"[Data Breach]\",\n" +
      "            \"filter\": false\n" +
      "        }\n" +
      "    ],\n" +
      "    \"filters\": [{\n" +
      "            \"colId\": \"Event.Data_Breach.Impact\",\n" +
      "            \"filterType\": \"text\",\n" +
      "            \"type\": \"contains\",\n" +
      "            \"filter\": \"OS\"\n" +
      "        }\n" +
      "    ],\n" +
      "    \"dataType\": \"Event.Data_Breach\",\n" +
      "    \"from\": 0,\n" +
      "    \"to\": 100,\n" +
      "    \"sortCol\": \"Event.Data_Breach.Impact\",\n" +
      "    \"sortDir\": \"+desc\"\n" +
      "}\n";
  String phase1QueryStr = "\n"
      + "\n"
      + "def rulesStr = '''\n"
      + "\n"
      + "{\n"
      + "  \"updatereq\":\n"
      + "  {\n"
      + "\n"
      + "    \"vertices\":\n"
      + "\t[\n"
      + "\t  {\n"
      + "\t\t\"label\": \"Person.Natural\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Person.Natural.Full_Name\"\n"
      + "\t\t   ,\"val\": \"${First_Name?.toUpperCase()?.trim()} ${Last_Name?.toUpperCase()?.trim()}\"\n"
      + "\t\t   ,\"predicate\": \"eq\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Full_Name_fuzzy\"\n"
      + "\t\t   ,\"val\": \"${First_Name?.toUpperCase()?.trim()} ${Last_Name?.toUpperCase()?.trim()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Last_Name\"\n"
      + "\t\t   ,\"val\": \"${Last_Name?.toUpperCase()?.trim()}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Date_Of_Birth\"\n"
      + "\t\t   ,\"val\": \"${DateofBirth}\"\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Gender\"\n"
      + "\t\t   ,\"val\": \"${Sex.toUpperCase()}\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Customer_ID\"\n"
      + "\t\t   ,\"val\": \"${Customer_ID}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Title\"\n"
      + "\t\t   ,\"val\": \"${'MALE' == Sex.toUpperCase()? 'MR':'MS'}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Nationality\"\n"
      + "\t\t   ,\"val\": \"GB\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Location.Address\"\n"
      + "\t   ,\"props\":\n"
      + "\t    [\n"
      + "\t      {\n"
      + "\t    \t\"name\": \"Location.Address.Full_Address\"\n"
      + "\t       ,\"val\": \"${Address}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\n"
      + "\t      }\n"
      + "\t     ,{\n"
      + "\t    \t\"name\": \"Location.Address.parser.postcode\"\n"
      + "\t       ,\"val\": \"${com.pontusvision.utils.PostCode.format(Post_Code)}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\n"
      + "\t      }\n"
      + "\t     ,{\n"
      + "\t    \t\"name\": \"Location.Address.parser\"\n"
      + "\t       ,\"val\": \"${Address}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"com.pontusvision.utils.LocationAddress\"\n"
      + "\n"
      + "\t      }\n"
      + "\t     ,{\n"
      + "\t    \t\t\"name\": \"Location.Address.Post_Code\"\n"
      + "\t       ,\"val\": \"${com.pontusvision.utils.PostCode.format(Post_Code)}\"\n"
      + "\t       ,\"excludeFromSearch\": true\n"
      + "\t      }\n"
      + "\t    ]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Email_Address\"\n"
      + "\t\t,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Email_Address.Email\"\n"
      + "\t\t   ,\"val\": \"${Email_address}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Insurance_Policy\"\n"
      + "\t\t,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Insurance_Policy.Number\"\n"
      + "\t\t   ,\"val\": \"${Policynumber}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Object.Insurance_Policy.Type\"\n"
      + "\t\t   ,\"val\": \"${PolicyType}\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\t\"name\": \"Object.Insurance_Policy.Status\"\n"
      + "\t\t   ,\"val\": \"${PolicyStatus}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t    \"name\": \"Object.Insurance_Policy.Renewal_Date\"\n"
      + "\t\t   ,\"val\": \"${RenewalDate}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t    \"name\": \"Object.Insurance_Policy.Product_Type\"\n"
      + "\t\t   ,\"val\": \"${TypeOfinsurance}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Event.Group_Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_Start_Date\"\n"
      + "\t\t   ,\"val\": \"${currDate}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"excludeFromSearch\": false\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_End_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"CRM System CSV File\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Structured Data Insertion\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Event.Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"CRM System CSV File\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Structured Data Insertion\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Domain_b64\"\n"
      + "\t\t   ,\"val\": \"${original_request?.bytes?.encodeBase64()?.toString()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Metadata_Create_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\n"
      + "\t  ,{\n"
      + "\t\t\"label\": \"Event.Consent\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Consent.Status\"\n"
      + "\t\t   ,\"val\": \"${Permisssion_to_Contact?'Consent': 'No Consent'}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Consent.Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\t   ,{\n"
      + "\t\t\"label\": \"Object.Privacy_Notice\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Privacy_Notice.Who_Is_Collecting\"\n"
      + "\t\t   ,\"val\": \"[CRM System]\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t  }\t   \n"
      + "\t\t]\n"
      + "\t  } \n"
      + "\n"
      + "\t]\n"
      + "   ,\"edges\":\n"
      + "    [\n"
      + "      { \"label\": \"Uses_Email\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Object.Email_Address\" }\n"
      + "     ,{ \"label\": \"Lives\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Location.Address\"  }\n"
      + "     ,{ \"label\": \"Has_Policy\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Object.Insurance_Policy\"  }\n"
      + "\t ,{ \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Location.Address\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "     ,{ \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "     ,{ \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Event.Group_Ingestion\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "     ,{ \"label\": \"Consent\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Event.Consent\"  }\n"
      + "     ,{ \"label\": \"Has_Privacy_Notice\", \"fromVertexLabel\": \"Event.Consent\", \"toVertexLabel\": \"Object.Privacy_Notice\"  }\n"
      + "\t \n"
      + "    ]\n"
      + "  }\n"
      + "}\n"
      + "'''\n"
      + "StringBuffer sb = new StringBuffer ()\n"
      + "\n"
      + "try{\n"
      + "    ingestRecordListUsingRules(graph, App.g, listOfMaps, rulesStr, sb)\n"
      + "}\n"
      + "catch (Throwable t){\n"
      + "    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)\n"
      + "\n"
      + "    sb.append(\"\\n$t\\n$stackTrace\")\n"
      + "    throw new Throwable (sb.toString())\n"
      + "\n"
      + "\n"
      + "}\n"
      + "sb.toString()\n"
      + "\n";
  String queryStr2 = ""
      + "\n"
      + "def rulesStr = '''\n"
      + "{\n"
      + "  \"updatereq\":\n"
      + "  {\n"
      + "    \"vertices\":\n"
      + "\t[\n"
      + "\t  {\n"
      + "\t\t\"label\": \"Person.Natural\"\n"
      + "\t\t,\"percentageThreshold\": %s\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Person.Natural.Full_Name_fuzzy\"\n"
      + "\t\t   ,\"val\": \"${person}\"\n"
      + "\t\t   ,\"predicate\": \"textContainsFuzzy\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"postProcessor\": \"${it?.toUpperCase()?.trim()}\"\n"
      + "\t\t   \n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Last_Name\"\n"
      + "\t\t   ,\"val\": \"${person}\"\n"
      + "\t\t   ,\"predicate\": \"textContainsFuzzy\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"postProcessor\": \"${it?.toUpperCase()?.trim()}\"\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Location.Address\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Location.Address.parser.postcode\"\n"
      + "\t\t   ,\"val\": \"${postcode}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"postProcessorVar\": \"eachPostCode\"\n"
      + "\t\t   ,\"postProcessor\": \"${com.pontusvision.utils.PostCode.format(eachPostCode)}\"\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Email_Address\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Email_Address.Email\"\n"
      + "\t\t   ,\"val\": \"${email}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Insurance_Policy\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Insurance_Policy.Number\"\n"
      + "\t\t   ,\"val\": \"${policy_number}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Event.Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"Outlook PST Files\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Unstructured Data Insertion\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Domain_b64\"\n"
      + "\t\t   ,\"val\": \"${original_request?.bytes?.encodeBase64()?.toString()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Domain_Unstructured_Data_b64\"\n"
      + "\t\t   ,\"val\": \"${content?.bytes?.encodeBase64()?.toString()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Metadata_Create_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t   \n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "     ,{\n"
      + "\t\t\"label\": \"Event.Group_Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_Start_Date\"\n"
      + "\t\t   ,\"val\": \"${currDate}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_End_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"excludeFromSubsequenceSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"Outlook PST Files\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Unstructured Data Insertion\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\n"
      + "\t]\n"
      + "   ,\"edges\":\n"
      + "    [\n"
      + "      { \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "     ,{ \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Event.Group_Ingestion\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "    ]\n"
      + "  }\n"
      + "}\n"
      + "'''\n"
      + "\n"
      + "groovy.json.JsonSlurper slurper = new groovy.json.JsonSlurper();\n"
      + "\n"
      + "\n"
      + "def bindings = [:];\n"
      + "\n"
      + "bindings['metadataController'] = \"${metadataController}\";\n"
      + "bindings['metadataGDPRStatus'] = \"${metadataGDPRStatus}\";\n"
      + "bindings['metadataLineage'] = \"${metadataLineage}\";\n"
      + "bindings['address'] = \"${nlp_res_address}\";\n"
      + "//bindings['company'] = \"${nlp_res_company?:[]}\";\n"
      + "bindings['cred_card'] = \"${nlp_res_cred_card}\";\n"
      + "bindings['email'] = \"${nlp_res_emailaddress}\";\n"
      + "bindings['location'] = \"${nlp_res_location}\";\n"
      + "bindings['currDate'] = \"${currDate}\";\n"
      + "\n"
      + "def parsedContent = slurper.parseText(content);\n"
      + "\n"
      + "bindings['content'] = parsedContent.text;\n"
      + "\n"
      + "bindings['city'] = \"${nlp_res_city}\";\n"
      + "\n"
      + "\n"
      + "\n"
      + "\n"
      + "\n"
      + "def personFilter = ['Name insured person: ','1: ','Self','name: ','0','1','Name insured 1: ','Name: ','2','0: ','1: ',' 1: ']\n"
      + "// def personNamesRawList = slurper.parseText(\"${nlp_res_person}\")\n"
      + "// def personNameSplitList = []\n"
      + "// personNamesRawList?.each{ personName ->\n"
      + "// \n"
      + "//   def passedFilter = personName != null && personName.length() > 2 && !( personName in personFilter);\n"
      + "// \n"
      + "//   if (passedFilter){\n"
      + "//     personNameSplitList << personName;\n"
      + "//     String[] personNameSplit = personName?.split()\n"
      + "//     personNameSplit?.each{ splitPersonName ->\n"
      + "//   \n"
      + "//       if (splitPersonName != \"\")\n"
      + "//       personNameSplitList << splitPersonName\n"
      + "//     }\n"
      + "//   }\n"
      + "// }\n"
      + "\n"
      + "\n"
      + "\n"
      + "bindings['person'] = \"${com.pontusvision.utils.NLPCleaner.filter(nlp_res_person,(boolean)true,(Set<String>)personFilter) as String}\";\n"
      + "// bindings['person'] = \"${nlp_res_person}\";\n"
      + "bindings['phone'] = \"${nlp_res_phone}\";\n"
      + "bindings['postcode'] = \"${nlp_res_post_code}\";\n"
      + "bindings['policy_number'] = \"${nlp_res_policy_number}\";\n"
      + "\n"
      + "\n"
      + "\n"
      + "StringBuffer sb = new StringBuffer ()\n"
      + "\n"
      + "try{\n"
      + "  sb.append(\"\\n\\nbindings: ${bindings}\");\n"
      + "   \n"
      + "  ingestDataUsingRules(graph, g, bindings, rulesStr, sb)\n"
      + "}\n"
      + "catch (Throwable t){\n"
      + "    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)\n"
      + "\n"
      + "    sb.append(\"\\n$t\\n$stackTrace\")\n"
      + "\t\n"
      + "\tthrow new Throwable(sb.toString())\n"
      + "\n"
      + "\n"
      + "}\n"
      + "sb.toString()";
  String queryStr3 = ""
      + "\n"
      + "def rulesStr = '''\n"
      + "{\n"
      + "  \"updatereq\":\n"
      + "  {\n"
      + "    \"vertices\":\n"
      + "\t[\n"
      + "\t  {\n"
      + "\t\t\"label\": \"Person.Natural\"\n"
      + "\t\t,\"percentageThreshold\": %s\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Person.Natural.Full_Name_fuzzy\"\n"
      + "\t\t   ,\"val\": \"${person}\"\n"
      + "\t\t   ,\"predicate\": \"idxRaw:Person.Natural.MixedIdx\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"postProcessor\": \"v.'Person.Natural.Full_Name_fuzzy':${it?.trim()}~\"\n"
      + "\t\t   \n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Person.Natural.Last_Name\"\n"
      + "\t\t   ,\"val\": \"${person}\"\n"
      + "\t\t   ,\"predicate\": \"textContainsFuzzy\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"postProcessor\": \"${it?.toUpperCase()?.trim()}\"\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Location.Address\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Location.Address.parser.postcode\"\n"
      + "\t\t   ,\"val\": \"${postcode}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"postProcessorVar\": \"eachPostCode\"\n"
      + "\t\t   ,\"postProcessor\": \"${com.pontusvision.utils.PostCode.format(eachPostCode)}\"\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Email_Address\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Email_Address.Email\"\n"
      + "\t\t   ,\"val\": \"${email}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Object.Insurance_Policy\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Object.Insurance_Policy.Number\"\n"
      + "\t\t   ,\"val\": \"${policy_number}\"\n"
      + "\t\t   ,\"type\":\"[Ljava.lang.String;\"\n"
      + "\t\t   ,\"excludeFromUpdate\": true\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t]\n"
      + "\n"
      + "\t  }\n"
      + "\t ,{\n"
      + "\t\t\"label\": \"Event.Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"Outlook PST Files\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Unstructured Data Insertion\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Domain_b64\"\n"
      + "\t\t   ,\"val\": \"${original_request?.bytes?.encodeBase64()?.toString()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Domain_Unstructured_Data_b64\"\n"
      + "\t\t   ,\"val\": \"${content?.bytes?.encodeBase64()?.toString()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Ingestion.Metadata_Create_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t   \n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "     ,{\n"
      + "\t\t\"label\": \"Event.Group_Ingestion\"\n"
      + "\t   ,\"props\":\n"
      + "\t\t[\n"
      + "\t\t  {\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_Start_Date\"\n"
      + "\t\t   ,\"val\": \"${currDate}\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Metadata_End_Date\"\n"
      + "\t\t   ,\"val\": \"${new Date()}\"\n"
      + "\t\t   ,\"excludeFromSearch\": true\n"
      + "\t\t   ,\"excludeFromSubsequenceSearch\": true\n"
      + "\t\t   ,\"type\": \"java.util.Date\"\n"
      + "\t\t  }\n"
      + "\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Type\"\n"
      + "\t\t   ,\"val\": \"Outlook PST Files\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t\t ,{\n"
      + "\t\t\t\"name\": \"Event.Group_Ingestion.Operation\"\n"
      + "\t\t   ,\"val\": \"Unstructured Data Insertion\"\n"
      + "\t\t   ,\"mandatoryInSearch\": true\n"
      + "\t\t  }\n"
      + "\t   \n"
      + "\t\t]\n"
      + "\t  }\n"
      + "\n"
      + "\t]\n"
      + "   ,\"edges\":\n"
      + "    [\n"
      + "      { \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Person.Natural\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "     ,{ \"label\": \"Has_Ingestion_Event\", \"fromVertexLabel\": \"Event.Group_Ingestion\", \"toVertexLabel\": \"Event.Ingestion\"  }\n"
      + "    ]\n"
      + "  }\n"
      + "}\n"
      + "'''\n"
      + "\n"
      + "groovy.json.JsonSlurper slurper = new groovy.json.JsonSlurper();\n"
      + "\n"
      + "\n"
      + "def bindings = [:];\n"
      + "\n"
      + "bindings['metadataController'] = \"${metadataController}\";\n"
      + "bindings['metadataGDPRStatus'] = \"${metadataGDPRStatus}\";\n"
      + "bindings['metadataLineage'] = \"${metadataLineage}\";\n"
      + "bindings['address'] = \"${nlp_res_address}\";\n"
      + "//bindings['company'] = \"${nlp_res_company?:[]}\";\n"
      + "bindings['cred_card'] = \"${nlp_res_cred_card}\";\n"
      + "bindings['email'] = \"${nlp_res_emailaddress}\";\n"
      + "bindings['location'] = \"${nlp_res_location}\";\n"
      + "bindings['currDate'] = \"${currDate}\";\n"
      + "\n"
      + "def parsedContent = slurper.parseText(content);\n"
      + "\n"
      + "bindings['content'] = parsedContent.text;\n"
      + "\n"
      + "bindings['city'] = \"${nlp_res_city}\";\n"
      + "\n"
      + "\n"
      + "\n"
      + "\n"
      + "\n"
      + "def personFilter = ['Name insured person: ','1: ','Self','name: ','0','1','Name insured 1: ','Name: ','2','0: ','1: ',' 1: ']\n"
      + "// def personNamesRawList = slurper.parseText(\"${nlp_res_person}\")\n"
      + "// def personNameSplitList = []\n"
      + "// personNamesRawList?.each{ personName ->\n"
      + "// \n"
      + "//   def passedFilter = personName != null && personName.length() > 2 && !( personName in personFilter);\n"
      + "// \n"
      + "//   if (passedFilter){\n"
      + "//     personNameSplitList << personName;\n"
      + "//     String[] personNameSplit = personName?.split()\n"
      + "//     personNameSplit?.each{ splitPersonName ->\n"
      + "//   \n"
      + "//       if (splitPersonName != \"\")\n"
      + "//       personNameSplitList << splitPersonName\n"
      + "//     }\n"
      + "//   }\n"
      + "// }\n"
      + "\n"
      + "\n"
      + "\n"
      + "bindings['person'] = \"${com.pontusvision.utils.NLPCleaner.filter(nlp_res_person,(boolean)true,(Set<String>)personFilter) as String}\";\n"
      + "// bindings['person'] = \"${nlp_res_person}\";\n"
      + "bindings['phone'] = \"${nlp_res_phone}\";\n"
      + "bindings['postcode'] = \"${nlp_res_post_code}\";\n"
      + "bindings['policy_number'] = \"${nlp_res_policy_number}\";\n"
      + "\n"
      + "\n"
      + "\n"
      + "StringBuffer sb = new StringBuffer ()\n"
      + "\n"
      + "try{\n"
      + "  sb.append(\"\\n\\nbindings: ${bindings}\");\n"
      + "   \n"
      + "  ingestDataUsingRules(graph, g, bindings, rulesStr, sb)\n"
      + "}\n"
      + "catch (Throwable t){\n"
      + "    String stackTrace = org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)\n"
      + "\n"
      + "    sb.append(\"\\n$t\\n$stackTrace\")\n"
      + "\t\n"
      + "\tthrow new Throwable(sb.toString())\n"
      + "\n"
      + "\n"
      + "}\n"
      + "sb.toString()";

  @AfterClass
  public static void after() throws Exception {
    App.gserver.stop().join();
    App.oServer.shutdown();
    server.stop();

  }

  @BeforeClass
  public static void before() throws Exception {
    Path resourceDirectory = Paths.get(".");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    String jpostalDataDir = Paths.get(absolutePath, "jpostal", "libpostal").toString();
    System.setProperty("user.dir", absolutePath);
    System.setProperty("ORIENTDB_ROOT_PASSWORD", "pa55word");
    System.setProperty("ORIENTDB_HOME", absolutePath);
    System.setProperty("pg.jpostal.datadir", jpostalDataDir);

    server = App.createJettyServer();

    server.start();
    App.init(Paths.get(absolutePath, "config", "gremlin-server.yaml").toString());



  }

  /**
   * Rigourous Test :-)
   */
  @Test
  public void testApp() {
    Gson gson = new Gson();
    RecordRequest req = gson.fromJson(jsonReq, RecordRequest.class);

    req.search.vid = "#-1:-1";
    req.search.relationship = "has_server";
    req.search.direction = "->";

    String sqlCount = req.getSQL(true);

    String expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1)";

    assertEquals(expectedSqlCount, sqlCount);

    String sql = req.getSQL(false);
    String expectedSql = "SELECT @rid as id,`Event.Data_Breach.Description`,`Event.Data_Breach.Impact`  FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ) ORDER BY `Event.Data_Breach.Impact` DESC SKIP 0 LIMIT 100";
    assertEquals(expectedSql, sql);

    req.search.direction = "<-";
    sqlCount = req.getSQL(true);

    expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(IN('has_server')) FROM #-1:-1)";
    assertEquals(expectedSqlCount, sqlCount);

    req.search.vid = null;
    req.search.direction = null;
    req.search.relationship = null;
    sqlCount = req.getSQL(true);
    expectedSqlCount = "SELECT COUNT(*)  FROM `" +
        req.dataType +
        "`  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ";
    assertEquals(expectedSqlCount, sqlCount);


  }

  @Test
  public void test1() throws InterruptedException {


    String res = null;
    try {
      res = App.executor.eval("App.g.V().hasNext();").get().toString();
      System.out.println(res);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }

  }


  @Test
  public void test2() throws InterruptedException {


    String res = null;
    try {
      Path resourceDirectory = Paths.get(".");
      String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
      String csvPath = Paths.get(pwdAbsolutePath, "src", "test", "resources", "phase1.csv").toString();

      Reader in = new FileReader(csvPath);
      Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

      List<Map> listOfMaps = new ArrayList<>();
      for (CSVRecord record : records) {
        Map recMap = record.toMap();
        recMap.put("currDate", new Date().toString());
        listOfMaps.add(recMap);
      }


      Map<String, Object> bindings = new HashMap() {{
        put("listOfMaps", listOfMaps);
      }};

      res = App.executor.eval(phase1QueryStr, bindings).get().toString();
      System.out.println(res);

    } catch (ExecutionException | IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }

  static Gson gson = new Gson();

  //play with tests :-) !!
  @Test
  public void test3() throws InterruptedException {

    jsonTestUtil("totvs1.json", "$.objs", "totvs_protheus_sa1_clientes");
//    jsonTestUtil("totvs2.json", "$.objs", "totvs_protheus_sa1_clientes");

    try {
      String userId =
          App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('COMIDAS 1')).next().id().toString()")
              .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
          "App.g.V().has('Person.Natural.Full_Name',eq('COMIDAS 1')).next().id().toString()");
      JsonObject obj = JsonParser.parseString( res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
      Gson gson = new Gson();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
      }



      assertEquals(userId, ((obj.get("result").getAsJsonObject())
          .getAsJsonObject().get("data").getAsJsonObject()).get("@value").getAsJsonArray().get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  //@Test
  public void test4() throws InterruptedException {

    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");
//    jsonTestUtil("ploomes1.json", "$.value", "ploomes_clientes");

    try {
      String userId =
              App.executor.eval("App.g.V().has('Person.Natural.Full_Name',eq('COMIDAS 1')).next().id().toString()")
                      .get().toString();

      Resource res = new Resource();
      GremlinRequest gremlinReq = new GremlinRequest();
      gremlinReq.setGremlin(
              "App.g.V().has('Person.Natural.Full_Name',eq('COMIDAS 1')).next().id().toString()");
      JsonObject obj = JsonParser.parseString( res.gremlinQuery(gson.toJson(gremlinReq))).getAsJsonObject();
      Gson gson = new Gson();
      String stringifiedOutput = gson.toJson(obj);
      String res2;
      try {
        HttpClient client = HttpClients.createMinimal();

        HttpPost request = new HttpPost(URI.create("http://localhost:3001/home/gremlin"));

        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        StringEntity data = new StringEntity(gson.toJson(gremlinReq));
//        SerializableEntity data = new SerializableEntity(req);

        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(request).getEntity().getContent());
        System.out.println(res2);


        HttpGet getRequest = new HttpGet(URI.create("http://localhost:3001/home/hello"));

//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Accept", "application/json");

//        StringEntity getData = new StringEntity(gson.toJson(req));
//        SerializableEntity data = new SerializableEntity(req);

//        request.setEntity(data);


        res2 = IOUtils.toString(client.execute(getRequest).getEntity().getContent());


        System.out.println(res2);

      } catch (IOException e) {
        e.printStackTrace();
      }



      assertEquals(userId, ((obj.get("result").getAsJsonObject())
              .getAsJsonObject().get("data").getAsJsonArray()).get(0).getAsString());

      Map<String, Object> bindings = new HashMap() {{
        put("pg_id", userId);
        put("pg_templateText", "eyUgc2V0IHBvc3NpYmxlTWF0Y2hlcyA9IHB2OnBvc3NpYmxlTWF0Y2hlcyhjb250ZXh0LmlkLCd7Ik9iamVjdC5FbWFpbF9BZGRyZXNzIjogMTAuNSwgIk9iamVjdC5JZGVudGl0eV9DYXJkIjogOTAuNSwgIkxvY2F0aW9uLkFkZHJlc3MiOiAxMC4xLCAiT2JqZWN0LlBob25lX051bWJlciI6IDEuMCwgIk9iamVjdC5TZW5zdGl2ZV9EYXRhIjogMTAuMCwgIk9iamVjdC5IZWFsdGgiOiAxLjAsICJPYmplY3QuQmlvbWV0cmljIjogNTAuMCAsICJPYmplY3QuSW5zdXJhbmNlX1BvbGljeSI6IDEuMH0nKSAlfQp7JSBzZXQgbnVtTWF0Y2hlcyA9IHBvc3NpYmxlTWF0Y2hlcy5zaXplKCkgJX0KeyUgaWYgbnVtTWF0Y2hlcyA9PSAwICV9Cnt7IGNvbnRleHQuUGVyc29uX05hdHVyYWxfRnVsbF9OYW1lfX0gw6kgbyDDum5pY28gcmVnaXN0cm8gbm8gc2lzdGVtYS4KCnslIGVsc2UgJX0Ke3sgY29udGV4dC5QZXJzb25fTmF0dXJhbF9GdWxsX05hbWV9fSBDb3JyZXNwb25kZSBwb3RlbmNpYWxtZW50ZSBhIHt7IG51bU1hdGNoZXMgfX0gcmVnaXN0cm97JS0gaWYgbnVtTWF0Y2hlcyAhPSAxIC0lfXN7JSBlbmRpZiAlfS4KCgoKICB7eyAiPHRhYmxlIHN0eWxlPSdtYXJnaW46IDVweCc+PHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+VGl0dWxhcjwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UGVyY2VudHVhbDwvdGg+PHRoIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+UHJvcHJpZWRhZGVzIGVtIENvbXVtPC90aD48L3RyPiIgfX0KICB7JSBmb3IgaXRlbSBpbiBwb3NzaWJsZU1hdGNoZXMuZW50cnlTZXQoKSAlfQogIHt7ICAiPHRyIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPjx0ZCBzdHlsZT0nYm9yZGVyOiAxcHggc29saWQgI2RkZGRkZDt0ZXh0LWFsaWduOiBsZWZ0O3BhZGRpbmc6IDhweDsnPiUuMmYlJTwvdGQ+PHRkIHN0eWxlPSdib3JkZXI6IDFweCBzb2xpZCAjZGRkZGRkO3RleHQtYWxpZ246IGxlZnQ7cGFkZGluZzogOHB4Oyc+JXM8L3RkPiIgfCBmb3JtYXQgKGl0ZW0ua2V5LlBlcnNvbl9OYXR1cmFsX0Z1bGxfTmFtZSAsIGl0ZW0udmFsdWUgKiAxMDAuMCwgaXRlbS5rZXkuTGFiZWxzX0Zvcl9NYXRjaCApIH19CiAgeyUgZW5kZm9yICV9CiAge3sgIjwvdGFibGU+IiB9fQoKeyUgZW5kaWYgJX0=");
      }};

      String report = App.executor.eval("renderReportInBase64(pg_id,pg_templateText)", bindings).get().toString();
      System.out.println(report);

    } catch (ExecutionException e) {
      e.printStackTrace();
      assertNull(e);

    }


  }

  //@Test
  // totvs + ploomes
  // test5

  public void jsonTestUtil(String jsonFile, String jsonPath, String ruleName) throws InterruptedException {

    String res = null;
    try {
      Path resourceDirectory = Paths.get(".");
      String pwdAbsolutePath = resourceDirectory.toFile().getAbsolutePath();
      String jsonFilePath = Paths.get(pwdAbsolutePath, "src", "test", "resources", jsonFile).toString();

      String jsonString = FileUtils.readFileToString(new File(jsonFilePath), "UTF-8");

      IngestionJsonObjArrayRequest req = new IngestionJsonObjArrayRequest();
      req.jsonString = jsonString;
      req.jsonPath = jsonPath;
      req.ruleName = ruleName;

      Ingestion ingestion = new Ingestion();

      res = ingestion.jsonObjArray(req);
      System.out.println(res);

    } catch (ExecutionException | IOException e) {
      e.printStackTrace();
      assertNull(e);
    }
  }
}
