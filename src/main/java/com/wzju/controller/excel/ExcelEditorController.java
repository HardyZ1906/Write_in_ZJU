package com.wzju.controller.excel;

import java.io.IOException;

import com.wzju.service.AccountService;
import com.wzju.service.ExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class OpenExcelResponse {
    public String code;
    public String content;

    public OpenExcelResponse(String code, String content) {
        this.code = code;
        this.content = content;
    }
}

class UploadExcelResponse {
    String code;

    public UploadExcelResponse(String code) {
        this.code = code;
    }
}

// class GetExcelFileResponse {
//     public String code;
//     public byte[] content;

//     public GetExcelFileResponse(String code, byte[] content) {
//         this.code = code;
//         this.content = content;
//     }
// }

// class WriteToCellResponse {
//     public String code;

//     public WriteToCellResponse(String code) {
//         this.code = code;
//     }
// }

// class SwitchToCellResponse {
//     public String code;
//     public String occupier;

//     public SwitchToCellResponse(String code, String occupier) {
//         this.code = code;
//         this.occupier = occupier;
//     }
// }

@RestController
public class ExcelEditorController {
    
    @Autowired
    AccountService accountService;

    @Autowired
    ExcelService excelService;

    @RequestMapping("/openExcel")
    @CrossOrigin
    public OpenExcelResponse openExcel(String token, String owner, String filename) {
        String username = accountService.validate(token);
        if (username == null) {
            return new OpenExcelResponse("400", null);
        } else {
            String occupier = excelService.obtain(username, owner, filename);
            if (occupier == null) {
                try {
                    String content = new String(excelService.getExcelFile(owner, filename));
                    return new OpenExcelResponse("200", content);
                } catch(IOException e) {
                    e.printStackTrace();
                    return new OpenExcelResponse("205", null);
                }
            } else {
                return new OpenExcelResponse("401", occupier);
            }
        }
    }

    @RequestMapping("/uploadExcel")
    @CrossOrigin
    public UploadExcelResponse uploadExcel(String token, String owner, String filename, String content) {
        String username = accountService.validate(token);
        if (username == null) {
            return new UploadExcelResponse("400");
        } else {
            String occupier = excelService.obtain(username, owner, filename);
            if (occupier == null) {
                try {
                    excelService.saveExcelFile(owner, filename, content);
                    return new UploadExcelResponse("200");
                } catch(IOException e) {
                    return new UploadExcelResponse("205");
                }
            } else {
                return new UploadExcelResponse("401");
            }
        }
    }

    @RequestMapping("/quitEditting")
    @CrossOrigin
    public void quitEditting(String token) {
        String username = accountService.validate(token);
        if (username != null) {
            excelService.quitEditting(username);
        }
    }

    // @RequestMapping("/getExcelFile")
    // @CrossOrigin
    // public GetExcelFileResponse getExcelFile(String token, String owner, String filename) {
    //     String username = accountService.validate(token);
    //     if (username == null) {
    //         return new GetExcelFileResponse("400", null);
    //     } else {
    //         try {
    //             return new GetExcelFileResponse("200", excelService.getExcelFile(owner, filename));
    //         } catch (IOException e) {
    //             System.out.println("IOException...");
    //             e.printStackTrace();
    //             return new GetExcelFileResponse("401", null);
    //         }
    //     }
    // }

    // @RequestMapping("/writeToCell")
    // @CrossOrigin
    // public WriteToCellResponse writeToCell(String token, String owner, String filename,
    //         int sheet, int row, int col, String content) {
    //     String username = accountService.validate(token);
    //     if (username == null) {
    //         return new WriteToCellResponse("400");
    //     } else {
    //         try {
    //             excelService.writeToCell(username, owner, filename, sheet, row, col, content);
    //             return new WriteToCellResponse("200");
    //         } catch (IOException e) {
    //             System.out.println("IOException...");
    //             e.printStackTrace();
    //             return new WriteToCellResponse("401");
    //         }
    //     }
    // }

    // @RequestMapping("/switchToCell")
    // @CrossOrigin
    // public SwitchToCellResponse switchToCell(String token, String owner, String filename,
    //         int srcSheet, int srcRow, int srcCol, int destSheet, int destRow, int destCol) {
    //     String username = accountService.validate(token);
    //     if (username == null) {
    //         return new SwitchToCellResponse("400", null);
    //     } else {
    //         String occupier = 
    //             excelService.switchToCell(username, owner, filename, srcSheet, srcRow, srcCol, destSheet, destRow, destCol);
    //         if (occupier == null) {
    //             return new SwitchToCellResponse("200", null);
    //         } else {
    //             return new SwitchToCellResponse("401", occupier);
    //         }
    //     }
    // }
}
