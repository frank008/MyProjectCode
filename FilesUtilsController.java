package com.dfdk.controller;

import com.dfdk.common.constants.RequestData;
import com.dfdk.common.utils.Result;
import com.dfdk.common.utils.parseUtils.parsekmz.KmzParseService;
import com.dfdk.entity.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/file")
@Slf4j
public class FilesUtilsController {

    @Value("${upladFile.uploadPath}")
    private String uploadPath;

    @Value("${upladFile.downloadPath}")
    private String downloadPath;

    /**限制访问目录*/
    @Value("${upladFile.rootPath}")
    private String rootPath;

    @Autowired
    private KmzParseService kmzParseService;

    /**⽂件上传*/
    @PostMapping("/uploadFile")
    @ResponseBody
    public Result<Object> FileUpload(@RequestParam("file") MultipartFile file) {

        Result<Object> result = new Result<>();
        HashMap<Object, Object> responseMap = new HashMap<>();
        responseMap.put("path", null); //返回文件路径
        result.setData(responseMap);
        result.setCode(200);
        //创建输入输出流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String fileName = file.getOriginalFilename();
        String path=null;
        try {
            String lastName = fileName.split("\\.")[1];
            //组装目录
            String dirPath = "/document/";
            if ("xls,xlsx,kmz,kml".contains(lastName)) {
                dirPath = "/KmzAndExcel/";
            }
            if ("bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,wmf,webp,avif,apng".contains(lastName)) {
                dirPath = "/images/";
            }
            //指定上传的位置
            path = uploadPath + dirPath;
            log.info("文件上传路径：" + path + fileName);
            responseMap.put("path", path + fileName); //返回文件路径
            //获取文件的输入流
            inputStream = file.getInputStream();
            //获取上传时的文件名
            result.setMsg(fileName + " 文件上传成功!");
            //注意是路径+文件名
            path=path + fileName;
            File targetFile = new File(path);
            //如果之前的 String path = "d:/upload/" 没有在最后加 / ，那就要在 path 后面 + "/"
            //判断文件父目录是否存在
            if (!targetFile.getParentFile().exists()) {
                //不存在就创建一个
                targetFile.getParentFile().mkdir();
            }
            // 获取文件的输出流
            outputStream = new FileOutputStream(targetFile);
            //最后使用资源访问器FileCopyUtils的copy方法拷贝文件
            FileCopyUtils.copy(inputStream, outputStream);

            if(path.toLowerCase().endsWith(".kmz")){
                kmzParseService.parseKmzOrKml(path);
                new File(path).delete();
            }
            if(path.toLowerCase().endsWith(".kml")){
                kmzParseService.parseAndInsertKmlToDB(path);
                new File(path).delete();
            }
            if(path.toLowerCase().endsWith(".xls")){

            }
            if(path.toLowerCase().endsWith(".xlsx")){

            }

        } catch (IOException e) {
            log.error("文件上传异常：", e);
        } finally {
            try {
                // 无论成功与否，都有关闭输入输出流
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**下载文件*/
    @RequestMapping("/downloadFile")
    public void downloadLocal(@RequestBody RequestData<HashMap<String, String>> requestData, HttpServletResponse response) throws IOException {
        String downloadPath = rootPath+requestData.getData().get("filePath");
        try {
            String tempPath=downloadPath.replaceAll("\\\\","/");
            if(!tempPath.contains(rootPath)){
                log.error("文件下载路径非法!");
                return  ;
            }

            // 读到流中
            InputStream inputStream = new FileInputStream(downloadPath);// 文件的存放路径
            response.reset();
            response.setContentType("application/octet-stream");
            String filename = new File(downloadPath).getName();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            inputStream.close();
        } catch (IOException e) {
            log.error("文件下载异常：",e);
        }
    }

    /**查询文件
     * <p>
     * path ,读取 path下的所有文件
     */
    @RequestMapping("/getAllFiles")
    @ResponseBody
    public Result<Object> getAllFiles(@RequestBody RequestData<HashMap<String, String>> requestData) {

        Result<Object> result = new Result<>();
        ArrayList<FileEntity> hashMapArrayList = new ArrayList<>();
        result.setData(hashMapArrayList);
        result.setCode(200);

        String filePath = requestData.getData().get("getFilesPath");
        String type = requestData.getData().get("type");
        if(type.equalsIgnoreCase("T")){
            filePath=uploadPath;
        }
        String tempPath=filePath.replaceAll("\\\\","/");
        if(!tempPath.contains(rootPath)){
            result.setMsg("文件路径非法!");
            result.setCode(500);
            return result;
        }

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                file.mkdirs();
            }

            if (null != filePath && !filePath.equalsIgnoreCase("")) {
                file = new File(filePath);
            }

            if (null == file) {
                result.setMsg("文件目录不存在! " + filePath);
                return result;
            }

            for (File listFile : file.listFiles()) {
                if (listFile.isDirectory()) {
                    for (File file1 : listFile.listFiles()) {
                        if (file1.isDirectory()) {
                            for (File file2 : file1.listFiles()) {
                                if (file2.isDirectory()) {
                                    for (File file3 : file2.listFiles()) {
                                        if (file3.isDirectory()) {
                                            for (File file4 : file3.listFiles()) {
                                                if (file4.isDirectory()) {
                                                    for (File file5 : file4.listFiles()) {
                                                        FileEntity fileEntity = new FileEntity();
                                                        fileEntity.fileName = file5.getName();
                                                        fileEntity.filePath = file5.getAbsolutePath();
                                                        hashMapArrayList.add(fileEntity);
                                                    }

                                                } else {
                                                    FileEntity fileEntity = new FileEntity();
                                                    fileEntity.fileName = file4.getName();
                                                    fileEntity.filePath = file4.getAbsolutePath();
                                                    hashMapArrayList.add(fileEntity);
                                                }
                                            }

                                        } else {

                                            FileEntity fileEntity = new FileEntity();
                                            fileEntity.fileName = file3.getName();
                                            fileEntity.filePath = file3.getAbsolutePath();
                                            hashMapArrayList.add(fileEntity);
                                        }
                                    }
                                } else {

                                    FileEntity fileEntity = new FileEntity();
                                    fileEntity.fileName = file2.getName();
                                    fileEntity.filePath = file2.getAbsolutePath();
                                    hashMapArrayList.add(fileEntity);
                                }
                            }
                        } else {

                            FileEntity fileEntity = new FileEntity();
                            fileEntity.fileName = file1.getName();
                            fileEntity.filePath = file1.getAbsolutePath();
                            hashMapArrayList.add(fileEntity);
                        }
                    }
                } else {
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.fileName = listFile.getName();
                    fileEntity.filePath = listFile.getAbsolutePath();
                    hashMapArrayList.add(fileEntity);
                }
            }
            result.setCount(hashMapArrayList.size());
        } catch (Exception e) {
           log.error("文件查询异常：",e);
        }


        return result;
    }
    /**删除文件*/
    @RequestMapping("/deleteFiles")
    @ResponseBody
    public Result<Object> deleteFiles(@RequestBody RequestData<HashMap<String, String>> requestData){
        Result<Object> result = new Result<>();
        result.success();
        try {
            String filePath = requestData.getData().get("filePath");
            String tempPath=filePath.replaceAll("\\\\","/");
            if(!tempPath.contains(rootPath)){
                result.error();
                result.setMsg("文件路径非法!");
                return result;
            }
            File file = new File(filePath);
            boolean delete = file.delete();
            if(delete){
                result.setMsg("文件删除成功!");
            }else {
                result.setCode(500);
                result.setMsg("文件删除失败!");
            }
        } catch (Exception e) {
            log.error("文件删除异常：",e);
        }
        return result;
    }
}

