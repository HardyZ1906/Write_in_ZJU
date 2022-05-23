package com.wzju.service;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

// import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


class EditorRecord {
    String editor;
    Date lastSession;

    public EditorRecord(String editor, Date lastSession) {
        this.editor = editor;
        this.lastSession = lastSession;
    }
}

class EditorCleanerThread extends Thread {
    private HashMap<String, EditorRecord> editorMap;

    @Override
    public void run() {
        for (;;) {
            System.out.println("Cleaning up editor info...");
            try {
                sleep(100_000);  // cleans up editor info every 100 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (Thread.currentThread().isInterrupted()) {
                System.out.println("EditorCleanerThread interrupted.");
            }

            for (Iterator<Map.Entry<String, EditorRecord>> it = editorMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, EditorRecord> item = it.next();
                if (System.currentTimeMillis() - item.getValue().lastSession.getTime() > 100_000) {
                    System.out.println("Removed <" + item.getKey() + ":" + item.getValue().editor + ">.");
                    it.remove();
                }
            }
        }
    }

    public EditorCleanerThread(HashMap<String, EditorRecord> editorMap) {
        this.editorMap = editorMap;
    }
}

@Service
public class ExcelService {

    // private HashMap<String, HashMap<Integer, String>> cellUsageMap;
    // /*--------------filename--------cell-----user-----------------*/

    private HashMap<String, EditorRecord> editorMap;
    private HashMap<String, Lock> fileMutexMap;
    private EditorCleanerThread cleanerThread;

    @Autowired
    MongoTemplate mongoTemplate;

    public String obtain(String username, String owner, String filename) {
        String path = "./excelfiles/" + owner + "/" + filename + ".xls";
        System.out.println("obtain: " + username + ", " + path);
        EditorRecord editorRecord = editorMap.get(path);
        if (editorRecord == null) {
            editorMap.put(path, new EditorRecord(username, new Date()));
            return null;
        } else if (editorRecord.editor.equals(username)) {
            editorRecord.lastSession = new Date();
            return null;
        } else {
            return editorRecord.editor;
        }
    }

    public void quitEditting(String username) {
        System.out.println("quitEditting: " + username);
        for (Iterator<Map.Entry<String, EditorRecord>> it = editorMap.entrySet().iterator(); it.hasNext();) {
            String editor = it.next().getValue().editor;
            if (editor != null &&  editor.equals(username)) {
                it.remove();
            }
        }
    }

    public void createUserDir(String username) {
        File wd = new File(System.getProperty("user.dir"));
        System.out.println("Current working directory is: " + wd);
        String pathname = "./excelfiles/" + username + "/";
        System.out.println("createUserDir: " + pathname);
        File path = new File(pathname);
        path.mkdirs();
    }

    public void createExcelFile(String owner, String filename) throws IOException {
        String path = "./excelfiles/" + owner + "/" + filename + ".xls";
        createExcelFile(path);
    }

    public void createExcelFile(String path) throws IOException {
        System.out.println("createExcelFile: " + path);
        FileOutputStream fos = new FileOutputStream(path);
        XSSFWorkbook xwb = new XSSFWorkbook();
        xwb.write(fos); xwb.close(); fos.close();
    }

    public byte[] getExcelFile(String owner, String filename) throws IOException {
        String path = "./excelfiles/" + owner + "/" + filename + ".xls";
        return getExcelFile(path);
    }

    public byte[] getExcelFile(String path) throws IOException {
        System.out.println("getExcelFile: " + path);
        if (fileMutexMap.get(path) == null) {
            fileMutexMap.put(path, new ReentrantLock());
        }
        Lock mutex = fileMutexMap.get(path);
        mutex.lock();
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        mutex.unlock();
        fis.close();
        return data;
    }

    public void saveExcelFile(String owner, String filename, String content) throws IOException {
        String path = "./excelfiles/" + owner + "/" + filename + ".xls";
        saveExcelFile(path, content);
    }

    public void saveExcelFile(String path, String content) throws IOException {
        System.out.println("saveExcelFile: " + path);
        if (fileMutexMap.get(path) == null) {
            fileMutexMap.put(path, new ReentrantLock());
        }
        Lock mutex = fileMutexMap.get(path);
        mutex.lock();
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content.getBytes());
        mutex.unlock();
        fos.close();
    }

    public void dropExcelFile(String owner, String filename) throws IOException {
        String path = "./excelfiles/" + owner + "/" + filename + ".xls";
        dropExcelFile(path);
    }

    public void dropExcelFile(String path) throws IOException {
        if (fileMutexMap.get(path) == null) {
            fileMutexMap.put(path, new ReentrantLock());
        }
        Lock mutex = fileMutexMap.get(path);
        mutex.lock();
        File f = new File(path);
        f.delete();
        fileMutexMap.remove(path);
        editorMap.remove(path);
        mutex.unlock();
    }

    // public String switchToCell(String username, String owner, String filename,
    //         int srcSheet, int srcRow, int srcCol, int destSheet, int destRow, int destCol) {
    //     String path = "./excelfiles/" + owner + "/" + filename + ".xls";
    //     int source = convert(srcSheet, srcRow, srcCol), destination = convert(destSheet, destRow, destCol);
    //     return switchToCell(username, path, source, destination);
    // }

    // public String switchToCell(String username, String path, int source, int destination) {
    //     if (cellUsageMap.get(path) == null) {
    //         cellUsageMap.put(path, new HashMap<>());
    //     }
    //     HashMap<Integer, String> excelMap = cellUsageMap.get(path);
    //     excelMap.remove(source);  // 让出单元格
    //     if (destination < 0) {  // 不占用其他单元格
    //         return null;
    //     } else if (!excelMap.containsKey(destination)) {  // 目标单元格尚未被占用，分配给申请者
    //         excelMap.put(destination, username);
    //         return null;
    //     } else {  // 目标单元格被占用，返回占用者
    //         return excelMap.get(destination);
    //     }
    // }

    // public Boolean writeToCell(String username, String owner, String filename,
    //         int sheet, int row, int col, String content) throws IOException {
    //     String path = "./excelfiles/" + owner + "/" + filename + ".xls";
    //     int coordinate = convert(row, col, sheet);
    //     if (!validate(username, path, coordinate)) {
    //         return false;
    //     } else {
    //         Lock mutex = fileMutexMap.get(path);
    //         mutex.lock();
    //         FileInputStream fis = new FileInputStream(path);
    //         XSSFWorkbook xwb = new XSSFWorkbook(fis);
    //         XSSFCell cell = xwb.getSheetAt(sheet).getRow(row).getCell(col);
    //         cell.setCellValue(content);
    //         FileOutputStream fos = new FileOutputStream(path);
    //         xwb.write(fos);
    //         mutex.unlock();
    //         fis.close(); fos.close(); xwb.close();
    //         return true;
    //     }
    // }

    public ExcelService() {
        // cellUsageMap = new HashMap<>();
        fileMutexMap = new HashMap<>();
        editorMap = new HashMap<>();
        cleanerThread = new EditorCleanerThread(editorMap);
        cleanerThread.run();
    }

    // private Boolean validate(String username, String path, int coordinate) {
    //     HashMap<Integer, String> excelMap;
    //     if ((excelMap = cellUsageMap.get(path)) == null) {
    //         return false;
    //     } else if (excelMap.get(coordinate) != username) {
    //         return false;
    //     } else {
    //         return true;
    //     }
    // }

    /* 把三个参数（表编号，行数，列数）压缩成一个整数 */
    // private int convert(int row, int col, int sheet) {
    //     return sheet * 65536 * 256 + col * 65536 + row;
    // }

    // private int getSheet(int coordinate) {
    //     return coordinate % (65536 * 256);
    // }

    // private int getRow(int coordinate) {
    //     return coordinate % 65536;
    // }

    // private int getCol(int coordinate) {
    //     return coordinate % (65536 * 256) / 65536;
    // }
}
