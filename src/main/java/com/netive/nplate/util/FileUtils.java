package com.netive.nplate.util;

import com.netive.nplate.domain.BoardFileDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {
    /** 오늘 날짜 */
    private final String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

    /** 업로드 경로 */
    private final String uploadPath = Paths.get("C:", "develop", "upload", today).toString();

    /**
     * 서버에 생성할 파일명을 처리할 랜덤 문자열 반환
     * @return 랜덤 문자열
     */
    private final String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 서버에 첨부 파일을 생성하고, 업로드 파일 목록 반환
     * @param files    - 파일 Array
     * @param boardIdx - 게시글 번호
     * @return 업로드 파일 목록
     */
    public List<BoardFileDTO> uploadFiles(MultipartFile[] files, Long boardIdx) {

        /* 파일이 비어있으면 비어있는 리스트 반환 */
        if (files[0].getSize() < 1) {
            return Collections.emptyList();
        }

        /* 업로드 파일 정보를 담을 비어있는 리스트 */
        List<BoardFileDTO> boardFileList = new ArrayList<>();

        /* uploadPath에 해당하는 디렉터리가 존재하지 않으면, 부모 디렉터리를 포함한 모든 디렉터리를 생성 */
        File dir = new File(uploadPath);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        /* 파일 개수만큼 forEach 실행 */
        for (MultipartFile file : files) {
            try {
                /* 파일 확장자 */
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                /* 서버에 저장할 파일명 (랜덤 문자열 + 확장자) */
                final String saveName = getRandomString() + "." + extension;

                /* 업로드 경로에 saveName과 동일한 이름을 가진 파일 생성 */
                File target = new File(uploadPath, saveName);
                file.transferTo(target);

                /* 파일 정보 저장 */
                BoardFileDTO boardFile = new BoardFileDTO();
                boardFile.setFileNo(boardIdx);
                boardFile.setFileNm(file.getOriginalFilename());
                boardFile.setFileNmTemp(saveName);

                /* 파일 정보 추가 */
                boardFileList.add(boardFile);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // end of for

        return boardFileList;
    }


    /**
     * 해당 경로의 이미지 파일 목록 반환
     * @param file
     * @return
     */
    public static List<File> getImgFileList(File file) {

        List<File> resultList = new ArrayList<>(); // 이미지 파일 저장할 리스트

        // 지정한 이미지폴더가 존재하지 않을 경우 빈 리스트 반환
        if(!file.exists()) {
            return resultList;
        }

        File[] list = file.listFiles(new FileFilter() {

            String strImgExt = "jpg|png|gif|bmp"; // 허용할 이미지 타입

            @Override
            public boolean accept(File pathname) { // 원하는 파일만 가져오기 위해 FileFilter 재정의

                boolean chkResult = false;
                if(pathname.isFile()) {
                    String ext = pathname.getName().substring(pathname.getName().lastIndexOf(".")+1);
                    chkResult = strImgExt.contains(ext.toLowerCase());
                } else {
                    chkResult = true;
                }

                return chkResult;
            }
        });

        for(File f : list) {
            if(f.isDirectory()) {
                // 폴더이면 이미지목록을 가져오는 현재 메소드를 재귀호출
                resultList.addAll(getImgFileList(f));
            } else {
                // 폴더가 아니고 파일이면 리스트(resultList)에 추가
                resultList.add(f);
            }
        }
        return resultList;
    }

    /**
     * 확장자를 제외한 파일 이름만 출력
     * @param filepath
     * @return
     */
    public static String getFileNameNoExt(String filepath) {
        String fileName = filepath.substring(0, filepath.lastIndexOf("."));
        return fileName;
    }

    /**
     * 파일 확장자만 출력
     * @param filepath
     * @return
     */
    public static String getFileExt(String filepath) {
        String ext = filepath.substring(filepath.lastIndexOf(".")+1);
        return ext;
    }

    /**
     * 파일패스에서 이미지 상대경로 출력
     * 절대 경로에서 이미지 폴더(images)를 중심으로 상대경로를 반환
     * 패스 : 절대경로/images/1-A/1-A_0.jpg
     * @param target
     * @return
     */
    public static String getImgSrc(File target) {
        String url = target.getPath().substring(target.getPath().lastIndexOf("images"));
        return url;
    }

    /**
     * 이미지를 포함하고있는 폴더의 이름 얻기.
     * @param target
     * @return
     */
    public static String getImgDirName(File target) {
        String url = getImgSrc(target);

        int comp = url.lastIndexOf("\\") - (url.indexOf("\\")+1);

        String dirName = "";
        if(comp < 0) {
            dirName = "이미지";
        } else {
            dirName = url.substring(url.indexOf("\\") + 1, url.lastIndexOf("\\"));
        }

        return dirName;
    }
}
