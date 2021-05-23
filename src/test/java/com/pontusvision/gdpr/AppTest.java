package com.pontusvision.gdpr;

import com.google.gson.Gson;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

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
    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        Gson gson = new Gson();
        RecordRequest req = gson.fromJson(jsonReq,RecordRequest.class);

        req.search.vid="#-1:-1";
        req.search.relationship = "has_server";
        req.search.direction="->";

        String sqlCount = req.getSQL(true);

        String expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1)";

        assertEquals( expectedSqlCount, sqlCount );

        String sql = req.getSQL(false);
        String expectedSql = "SELECT @rid as id,`Event.Data_Breach.Description`,`Event.Data_Breach.Impact`  FROM (SELECT EXPAND(OUT('has_server')) FROM #-1:-1  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ) ORDER BY `Event.Data_Breach.Impact` DESC SKIP 0 LIMIT 100";
        assertEquals( expectedSql, sql );

        req.search.direction="<-";
        sqlCount = req.getSQL(true);

        expectedSqlCount = "SELECT COUNT(*) FROM (SELECT EXPAND(IN('has_server')) FROM #-1:-1)";
        assertEquals( expectedSqlCount, sqlCount );

        req.search.vid = null;
        req.search.direction = null;
        req.search.relationship = null;
        sqlCount = req.getSQL(true);
        expectedSqlCount = "SELECT COUNT(*)  FROM `"+
                req.dataType +
                "`  WHERE (( `Event.Data_Breach.Impact`  containsText  'OS' ) ) ";
        assertEquals( expectedSqlCount, sqlCount );


    }
}
