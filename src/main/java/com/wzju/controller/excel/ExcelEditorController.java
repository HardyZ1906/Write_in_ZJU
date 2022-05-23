package com.wzju.controller.excel;

import java.io.IOException;

import com.wzju.service.AccountService;
import com.wzju.service.ExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GetExcelFileResponse {
    public String code;
    public byte[] content;

    public GetExcelFileResponse(String code, byte[] content) {
        this.code = code;
        this.content = content;
    }
}

class WriteToCellResponse {
    public String code;

    public WriteToCellResponse(String code) {
        this.code = code;
    }
}

class SwitchToCellResponse {
    public String code;
    public String occupier;

    public SwitchToCellResponse(String code, String occupier) {
        this.code = code;
        this.occupier = occupier;
    }
}

@RestController
public class ExcelEditorController {
    
    @Autowired
    AccountService accountService;

    @Autowired
    ExcelService excelService;

    @RequestMapping("/getExcelFile")
    @CrossOrigin
    public GetExcelFileResponse getExcelFile(String token, String owner, String filename) {
        String username = accountService.validate(token);
        if (username == null) {
            return new GetExcelFileResponse("400", null);
        } else {
            try {
                return new GetExcelFileResponse("200", excelService.getExcelFile(owner, filename));
            } catch (IOException e) {
                System.out.println("IOException...");
                e.printStackTrace();
                return new GetExcelFileResponse("401", null);
            }
        }
    }

    @RequestMapping("/writeToCell")
    @CrossOrigin
    public WriteToCellResponse writeToCell(String token, String owner, String filename,
            int sheet, int row, int col, String content) {
        String username = accountService.validate(token);
        if (username == null) {
            return new WriteToCellResponse("400");
        } else {
            try {
                excelService.writeToCell(username, owner, filename, sheet, row, col, content);
                return new WriteToCellResponse("200");
            } catch (IOException e) {
                System.out.println("IOException...");
                e.printStackTrace();
                return new WriteToCellResponse("401");
            }
        }
    }

    @RequestMapping("/switchToCell")
    @CrossOrigin
    public SwitchToCellResponse switchToCell(String token, String owner, String filename,
            int srcSheet, int srcRow, int srcCol, int destSheet, int destRow, int destCol) {
        String username = accountService.validate(token);
        if (username == null) {
            return new SwitchToCellResponse("400", null);
        } else {
            String occupier = 
                excelService.switchToCell(username, owner, filename, srcSheet, srcRow, srcCol, destSheet, destRow, destCol);
            if (occupier == null) {
                return new SwitchToCellResponse("200", null);
            } else {
                return new SwitchToCellResponse("401", occupier);
            }
        }
    }
}
