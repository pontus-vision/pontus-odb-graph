package com.pontusvision.graphutils
// g.V().has('Metadata.Type.Event.Subject_Access_Request', 'Event.Subject_Access_Request').drop()

// g.V().has('Metadata.Type.Object.Identity_Card', 'Object.Identity_Card')
import groovy.util.slurpersupport.GPathResult

import java.nio.charset.StandardCharsets
import java.util.zip.DataFormatException
import java.util.zip.Inflater;


public class DrawIOGremlin {


  static byte[] uncompress(final byte[] input) throws DataFormatException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Inflater decompressor = new Inflater(true);
    try {
      decompressor.setInput(input);
      final byte[] buf = new byte[2048];
      while (!decompressor.finished()) {
        int count = 0;
        try {
          count = decompressor.inflate(buf);
        } catch (DataFormatException e) {
          e.printStackTrace();
          throw e;
        }
        bos.write(buf, 0, count);
      }
    } finally {
      decompressor.end();
    }
    return bos.toByteArray();
  }

  static XmlSlurper slurper = new groovy.util.XmlSlurper();

  static String drawIoFileToGremlin(File file) {

    //  def data = '''
//<mxfile modified="2020-07-14T16:46:57.784Z" host="Electron" agent="5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) draw.io/13.3.9 Chrome/83.0.4103.119 Electron/9.0.5 Safari/537.36" etag="VWVcN1rUYTgDKIbNsbCi" version="13.3.9" type="device"><diagram id="j_qr9nGa-FRXY_upCuxa" name="Page-1">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</diagram></mxfile>
//''';


    def preParsedXML = slurper.parse(file);

    return drawIoGPathResultToGremlin(preParsedXML);

  }

  static String drawIoGPathResultToGremlin(GPathResult preParsedXML) {

    String  base64Data = preParsedXML?.diagram?.text();

    byte[] decodedBase64Data = Base64.getDecoder().decode(base64Data as String);

    def xmlURLEncoded = new String(uncompress(decodedBase64Data));

    def xml = URLDecoder.decode(xmlURLEncoded,
      StandardCharsets.UTF_8.name());

    def parsedXML = slurper.parseText(xml);

    StringBuffer sb = new StringBuffer();

    def objMap = [:];
    def vertexMap = [:];

    parsedXML?.root?.object?.each {
      def id = it.attributes()['id'];
      def metadataType = it.attributes()['Metadata.Type']
      if (metadataType) {
        def trav = g.addV("${metadataType}");
        sb.append("""\ng.addV("${metadataType}")""")
        objMap[id] = it.attributes();
        it.attributes().each { name, val ->
          if (name.startsWith("Metadata") || name.startsWith(metadataType)) {
            sb.append("""\n  .property("${name}","${val}")""");
            trav = trav.property(name, "${val}");
          }
        }
        vertexMap[id] = trav.next();
      }
    }

    parsedXML.root.mxCell.each {
      def source = it.attributes()['source'];
      def label = it.attributes()['value'];
      def target = it.attributes()['target'];

      def sourceObj = objMap[source];
      def sourceType = sourceObj ? sourceObj['Metadata.Type'] : null;

      def targetObj = objMap[target];
      def targetType = targetObj ? targetObj['Metadata.Type'] : null;

      if (sourceType) {
        sb.append("${label} - from: ${source} (${sourceType}) to: ${target} (${targetType})\n")

        g.addE(label).from(vertexMap[source]).to(vertexMap[target]).next()

      }

    }

    return sb.toString()

  }


}
