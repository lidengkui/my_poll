package com.poll.service.business;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.io.Files;
import com.poll.ability.dto.RespMessage;
import com.poll.common.Constants;
import com.poll.common.ConstantsOfParamName;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.DateUtil;
import com.poll.common.util.RegularUtil;
import com.poll.common.util.StringUtil;
import com.poll.dao.mapper.StaffGpMapper;
import com.poll.dao.service.StaffGpService;
import com.poll.dao.service.StaffService;
import com.poll.entity.StaffEntity;
import com.poll.entity.StaffGpEntity;
import com.poll.entity.UserEntity;
import com.poll.service.module.UploadModuleService;
import com.poll.service.util.TransferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UploadService_V1 {

    protected Logger log = LogManager.getLogger();
    @Autowired
    protected UploadModuleService uploadModuleService;

    @Value("${uploadConf.configs.poll.limit}")
    protected int pollStaffColumnLimit;

    @Value("${uploadConf.configs.poll.batchSize}")
    protected int batchSize;

    protected String errorMsg = "第{0}行{1}列:{2}";

    //员工信息列
    protected final int pollStaffColumn = 3;

    @Autowired
    protected StaffService staffService;

    @Autowired
    protected StaffGpService staffGpService;

    @Autowired
    protected StaffGpMapper staffGpMapper;

    //包含头 1  A
    //校验包装对象
    class Validate {
        //最大列数
        public final int max;

        public Rule defaultRule;

        private Map<Integer, List<Rule>> rules = new HashMap<>();

        public Validate(int max, Rule defaultRule) {
            this.max = max;
            this.defaultRule = defaultRule;
        }

        public Validate add(int index, Rule rule) throws ApiBizException {
            if (index > max) {
                throwApiBizException("列[" + Constants.poll_COLUMN_MAP.get(index) + "]超过最大长度[" + max + "]");
            }
            List<Rule> indexRule = null;
            if (rules.containsKey(index)) {
                indexRule = rules.get(index);
                indexRule.remove(this.defaultRule);
            } else {
                indexRule = new ArrayList<>();
            }
            indexRule.add(rule);
            indexRule.add(this.defaultRule);
            this.rules.put(index, indexRule);
            return this;
        }

        public void doIt(String[] row, int currentRow) throws ApiBizException {
            if (row.length < max) {
                throwApiBizException("第" + (currentRow + 1) + "行数据列与模板不匹配，请修改后重试");
            }
            for (int i = 0; i < max; i++) {
                if (!this.rules.containsKey(i)) {
                    if (!this.defaultRule.check(row, i)) {
                        throwApiBizException(MessageFormat.format(errorMsg, (currentRow + 1), Constants.poll_COLUMN_MAP.get(i), this.defaultRule.misdesp));
                    }
                } else {
                    List<Rule> ruleList = this.rules.get(i);
                    for (int j = (ruleList.size() - 1); -1 < j; j--) {
                        Rule rule = ruleList.get(j);
                        if (!rule.check(row, i)) {
                            throwApiBizException(MessageFormat.format(errorMsg, (currentRow + 1), Constants.poll_COLUMN_MAP.get(i), rule.misdesp));
                        }
                    }
                }
            }
        }
    }

    //规则
    abstract class Rule {

        public final String misdesp;

        public Rule(String misdesp) {
            this.misdesp = misdesp;
        }

        //到底是传值校验还是根据自定义规则的下标来校验
        abstract boolean check(String[] row, int index);
    }

    //持有固定值
    abstract class HolderRule<T> extends Rule {
        public T value;

        public HolderRule(String misdesp) {
            super(misdesp);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RespMessage staff(JSONObject reqJo, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Long groupId = CheckUtil.checkParamSimpleFromTo(ConstantsOfParamName.GROUPId, ConstantsOfParamName.GROUPId_ALIAS, Long.class, request, null, false, null, "0", null, RegularUtil.pureNumReg);

        UserEntity currentUser = TransferUtil.parseUserInfoFromJson(reqJo);
        StaffGpEntity pollSgSnpEntity = staffGpService.selectOne(new EntityWrapper<StaffGpEntity>()
                .eq("company_id_sg", currentUser.getCompanyId()).eq("id_sg", groupId));
        if (pollSgSnpEntity == null) {
            throw new ApiBizException(MsgCode.C00000011.code, "非法上传");
        }

        uploadModuleService.handle(file, "poll", new UploadModuleService.Callback() {

            @Override
            public boolean call(String dataBasePath, JSONObject jo) throws Exception {
                //获得文件后缀
                String fileExtension = Files.getFileExtension(dataBasePath);

                Workbook workbook = null;
                FileInputStream fin = new FileInputStream(dataBasePath);
                if (Constants.FILE_EXTENSION_XLS.equalsIgnoreCase(fileExtension)) {
                    workbook = new HSSFWorkbook(fin);
                } else {
                    workbook = new XSSFWorkbook(fin);
                }
                Sheet sheet = workbook.getSheetAt(0);

                //获取行数
                int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
                if (rowCount > pollStaffColumnLimit) {
                    throwApiBizException("超过最大子订单数，请拆分文件重新上传");
                }
                if (rowCount == 0) {
                    throwApiBizException("未读取到数据，请检查后继续");
                }

                Validate validate = genValidate();
                int staffNum = 0;
                List<StaffEntity> staffEntities = new ArrayList<>(200);
                for (int i = 1; i <= rowCount; i++) {

                    Row row = sheet.getRow(i);
                    if (null == row) {
                        continue;
                    }

                    String fields[] = new String[pollStaffColumn];
                    for (int j = 0; j < pollStaffColumn; j++) {
                        if (row.getCell(j) == null || row.getCell(j).getCellTypeEnum() == CellType.BLANK) {

                            fields[j] = Constants.STR_BLANK;

                        } else if (row.getCell(j).getCellTypeEnum() == CellType.STRING) {

                            fields[j] = row.getCell(j).getStringCellValue();

                        } else if (row.getCell(j).getCellTypeEnum() == CellType.NUMERIC) {

                            Cell cell = row.getCell(j);
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                double d = cell.getNumericCellValue();
                                Date date = HSSFDateUtil.getJavaDate(d);
                                String dateStr = DateUtil.convertDate2Str(date, DateUtil.FORMATE_YYYY_MM_DD_HH_MM_SS_MINUS);
                                fields[j] = dateStr;
                                continue;
                            } else if (cell.getCellStyle().getDataFormat() == 0) {
                                DecimalFormat df = new DecimalFormat("0");
                                String tempStr = df.format(cell.getNumericCellValue());
                                fields[j] = tempStr;
                                continue;
                            }

                            DecimalFormat df = new DecimalFormat("0.00");
                            String tempStr = df.format(cell.getNumericCellValue());
                            fields[j] = tempStr;

                        } else {
                            fields[j] = Constants.STR_BLANK;
                        }
                    }

                    boolean jump = true;
                    for (String blank : fields) {
                        if (!StringUtils.isEmpty(blank)) {
                            jump = false;
                        }
                    }

                    if (!jump) {
                        staffNum++;
                        validate.doIt(fields, i);
                        //组装数据
                        StaffEntity staffEntity = new StaffEntity();
                        dataAssemble(staffEntity, fields);
                        staffEntity.setCompanyId(currentUser.getCompanyId());
                        staffEntity.setUserId(currentUser.getId());
                        staffEntity.setSgId(groupId);
                        staffEntities.add(staffEntity);
                        if (staffEntities.size() >= 200) {
                            try {
                                staffService.insertBatch(staffEntities);
                                staffEntities.clear();
                            } catch (Exception e) {
                                workbook.close();
                                throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
                            }
                        }
                    }
                }
                if (!staffEntities.isEmpty()) {
                    try {
                        staffService.insertBatch(staffEntities);
                        staffEntities = null;
                    } catch (Exception e) {
                        workbook.close();
                        throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
                    }
                }
                staffGpMapper.addGpMemberNumGpMemberNum(currentUser.getCompanyId(), groupId, staffNum);
                workbook.close();
                return true;
            }
        });
        return RespMessage.genSuccess();
    }

    /**
     * 数据组装
     *
     * @param staff
     * @param strings
     */
    public void dataAssemble(StaffEntity staff, String... strings) {
        staff.setName(strings[0]);
        staff.setMobile(Long.parseLong(strings[1]));
        staff.setRemark(strings[2]);
        Date now = new Date();
        staff.setCreateTime(now);
        staff.setUpdateTime(now);
    }

    /**
     * 抛出异常
     *
     * @param errorMsg
     */
    public void throwApiBizException(String errorMsg) throws ApiBizException {
        throw new ApiBizException(MsgCode.C00000001.code, errorMsg);
    }

    public Validate genValidate() throws ApiBizException {
        Validate validate = new Validate(pollStaffColumn, new Rule("no handle") {
            @Override
            boolean check(String[] row, int index) {
                return true;
            }
        });
        validate.add(0, new Rule("姓名不能为空，请修改后重试") {
            @Override
            boolean check(String[] row, int index) {
                if (StringUtils.isEmpty(row[index])) {
                    return false;
                }
                return true;
            }
        });
        validate.add(1, new Rule("手机号码长度必须为11位纯数字，请修改后重试") {
            @Override
            boolean check(String[] row, int index) {
                if (Pattern.compile(RegularUtil.phoneNoReg).matcher(row[index]).find()) {
                    return true;
                }
                return false;
            }
        });
        validate.add(1, new Rule("手机号码不能为空，请修改后重试") {
            @Override
            boolean check(String[] row, int index) {
                if (StringUtils.isEmpty(row[index])) {
                    return false;
                }
                return true;
            }
        });

        validate.add(2, new Rule("备注长度不能超过15位字符，请修改后重试") {
            @Override
            boolean check(String[] row, int index) {
                if (!StringUtils.isEmpty(row[index])) {
                    if (row[index].length() > 15) {
                        return false;
                    }
                }
                return true;
            }
        });
        return validate;
    }
}
