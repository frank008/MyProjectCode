package com.dfdk.common.utils.parseUtils.parsekmz;

import com.dfdk.common.utils.parseUtils.parsekmz.db.DbServicesUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/*
* 1、依赖
        <dependency>
			<groupId>jaxen</groupId>
			<artifactId>jaxen</artifactId>
			<version>1.1.1</version>
		</dependency>
		<dependency>
			<groupId>dom4j</groupId>
			<artifactId>dom4j</artifactId>
			<version>1.6.1</version>
		</dependency>
    2、解压kmz⽂件成kml
* */

@Slf4j
@Component
public class KmzParseService {
    @Value("${upladFile.rootPath}")
    public String destPath;

    @Autowired
    public DbServicesUtils dbServicesUtils;

    // 2.KMZ转KML
    public void parseKmzOrKml(String kmzPath) {

        try {
            log.info("**********************     【KMZ转kml开始】kmz路径：       **********************\n" + kmzPath);
            File file = new File(kmzPath);
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            InputStream inputStream = null;
            ZipEntry entry = null;
            Document doc = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = entry.getName();
                //获取所需文件的节点
                if (zipEntryName.equals("doc.kml")) {
                    inputStream = zipFile.getInputStream(entry);
                    SAXReader reader = new SAXReader();
                    doc = reader.read(inputStream);
                    inputStream.close();
                }
            }
            zipFile.close();
            zipInputStream.close();

            //删除原来的文件
            String filePath = destPath + "/doc.kml";
            new File(filePath).delete();

            //kmz 文件写入指定 路径
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");    // 指定XML编码
            XMLWriter writer = new XMLWriter(new FileWriter(filePath), format);
            writer.write(doc);
            writer.close();

            //开始解析kml 文件到数据库
            parseAndInsertKmlToDB(null);
        } catch (Exception e) {
           log.error("kmz解析失败：",e);

        }
    }

    //解析kml文件 写入数据库
    public void parseAndInsertKmlToDB(String path) {
        if(null==path||path.equalsIgnoreCase("")){
            path=destPath + "/doc.kml";
        }
        try {
            File file = new File(path);
            ParsingKmlUtil.parseKmlToPointItem(file);
            dbServicesUtils.parseDataNew(ParsingKmlUtil.newKmlPonitsObjsList, ParsingKmlUtil.newKmlLinesObjsMap);
            log.info("kmz/kml解析完成！ 删除文件"+file.getAbsolutePath());
            file.delete();
        } catch (Exception e) {
            log.error("解析kmz/kml文件异常!",e);
        }
    }
}
