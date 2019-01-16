package com.poll.service.business;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.poll.ability.dto.RespMessage;
import com.poll.common.Constants;
import com.poll.common.ConstantsOfParamName;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.RegularUtil;
import com.poll.dao.service.PollService;
import com.poll.dao.service.PollSgStfPrdSnpService;
import com.poll.dao.service.ProductStatisticsService;
import com.poll.entity.PollEntity;
import com.poll.entity.PollSgStfPrdSnpEntity;
import com.poll.entity.UserEntity;
import com.poll.entity.ext.ProductStatisticsEntity;
import com.poll.service.util.TransferUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;


@Service
public class DownloadService_V1 {

    protected Logger log = LogManager.getLogger();

    @Autowired
    protected PollService pollService;

    @Autowired
    protected ProductStatisticsService productStatisticsService;

    @Autowired
    protected PollSgStfPrdSnpService pollSgStfPrdSnpService;

    public RespMessage prdtStatistics(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long pollId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.POLLID, ConstantsOfParamName.POLLId_ALIAS, Long.class, request, null, false, null, "0", null, RegularUtil.pureNumReg);
        UserEntity currentUser = TransferUtil.parseUserInfoFromJson(reqJo);
        PollEntity pollEntity = pollService.selectOne(new EntityWrapper<PollEntity>().eq("company_id_p", currentUser.getCompanyId()).eq("id_p", pollId));
        if (pollEntity == null) {
            throw new ApiBizException(MsgCode.C00000011.code, MsgCode.C00000011.msg);
        }
        Resource pollTemplate = new ClassPathResource("poll/调研统计.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(pollTemplate.getInputStream());
        //样式
        //微软雅黑
        XSSFCellStyle microsoftStyle = setCellBorderAndFont(workbook, "微软雅黑", (short) 11);
        microsoftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        microsoftStyle.setAlignment(HorizontalAlignment.LEFT);
        microsoftStyle.setWrapText(true);

        //dengxian
        XSSFCellStyle dengXianStyle4BL = setCellBorderAndFont(workbook, "DengXian", (short) 12);
        dengXianStyle4BL.setVerticalAlignment(VerticalAlignment.BOTTOM);
        dengXianStyle4BL.setAlignment(HorizontalAlignment.LEFT);

        //dengxian
        XSSFCellStyle dengXianStyle4B = setCellBorderAndFont(workbook, "DengXian", (short) 12);
        dengXianStyle4B.setVerticalAlignment(VerticalAlignment.BOTTOM);


        //汇总工作簿
        XSSFSheet collectSheet = workbook.getSheet("汇总");

        List<ProductStatisticsEntity> collectData = productStatisticsService.listCollectByPollId(pollId);
        if (collectData != null && !collectData.isEmpty()) {
            int currentLine = 2;
            for (ProductStatisticsEntity item : collectData) {
                //数据
                Row row = collectSheet.createRow(currentLine);
                row.setHeightInPoints(16.5f);
                Cell cell0 = row.createCell(0, CellType.STRING);
                cell0.setCellStyle(microsoftStyle);
                cell0.setCellValue(item.getPrdtName() + (StringUtils.isEmpty(item.getPrdtExtName()) ? Constants.STR_BLANK : item.getPrdtExtName()));
                Cell cell1 = row.createCell(1, CellType.STRING);
                cell1.setCellStyle(microsoftStyle);
                cell1.setCellValue(item.getPrdtUnit());
                Cell cell2 = row.createCell(2, CellType.NUMERIC);
                cell2.setCellStyle(dengXianStyle4BL);
                cell2.setCellValue(item.getPrdtTotal());
                currentLine++;
            }
        }
        //明细
        XSSFSheet detailSheet = workbook.getSheet("明细");

        int currentLine = 2;
        int serialNumber = 1;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            List<PollSgStfPrdSnpEntity> stfPrdSnpEntities = pollSgStfPrdSnpService.listForPagination(pollId, (i-1)*200L, 200);
            if (stfPrdSnpEntities.isEmpty()) {
                break;
            }
            for (PollSgStfPrdSnpEntity item : stfPrdSnpEntities) {
                Row row = detailSheet.createRow(currentLine);
                row.setHeightInPoints(16.5f);
                Cell cell0 = row.createCell(0, CellType.NUMERIC);
                cell0.setCellStyle(dengXianStyle4B);
                cell0.setCellValue(serialNumber);
                Cell cell1 = row.createCell(1, CellType.NUMERIC);
                cell1.setCellStyle(dengXianStyle4B);
                cell1.setCellValue(item.getStfMobilePssps());
                Cell cell2 = row.createCell(2, CellType.STRING);
                cell2.setCellStyle(microsoftStyle);
                cell2.setCellValue(item.getPrdNamePssps() + (StringUtils.isEmpty(item.getPrdNameExtPssps()) ? Constants.STR_BLANK : item.getPrdNameExtPssps()));
                Cell cell3 = row.createCell(3, CellType.STRING);
                cell3.setCellStyle(microsoftStyle);
                cell3.setCellValue(item.getPrdUnitPssps());
                Cell cell4 = row.createCell(4, CellType.NUMERIC);
                cell4.setCellStyle(microsoftStyle);
                cell4.setCellValue(item.getPurcsNumPssps());
                currentLine++;
                serialNumber++;
            }
        }
        setResponse(response, pollEntity.getName() + "【" + pollEntity.getCode() + "】" + Constants.FILE_FMT_XLSX, null);
        //writer stream
        try {
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } finally {
            workbook.close();
        }

        return null;
    }

    /**
     * 设置文件下载响应Metadata
     */
    private void setResponse(HttpServletResponse response, String fileName, String fileSize) throws UnsupportedEncodingException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("multipart/form-data");
        //rfc231
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8"));//设置响应的文件名
        if (!StringUtils.isEmpty(fileSize)) {
            response.setHeader("Content-Length", fileSize);//设置文件大小
        }
    }

    private void writeResponse(HttpServletResponse response, File file) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private XSSFCellStyle setCellBorderAndFont(XSSFWorkbook workBook, String fontName, short fontSize) {
        XSSFCellStyle cell = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        cell.setBorderBottom(BorderStyle.THIN);
        cell.setBorderLeft(BorderStyle.THIN);
        cell.setBorderRight(BorderStyle.THIN);
        cell.setBorderTop(BorderStyle.THIN);
        cell.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(new Color(0, 0, 0)));
        cell.setBorderColor(XSSFCellBorder.BorderSide.LEFT, new XSSFColor(new Color(0, 0, 0)));
        cell.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, new XSSFColor(new Color(0, 0, 0)));
        cell.setBorderColor(XSSFCellBorder.BorderSide.TOP, new XSSFColor(new Color(0, 0, 0)));
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        cell.setFont(font);
        return cell;
    }

}
