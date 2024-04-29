package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.*;

import example.com.moneymergebe.domain.record.entity.Record;
import example.com.moneymergebe.domain.record.entity.RecordComment;
import example.com.moneymergebe.global.exception.GlobalException;

public class RecordCommentValidator {
    public static void validate(RecordComment recordComment){
        if(checkIsNull(recordComment)) throw new GlobalException(NOT_FOUND_COMMENT);
    }

    public static void checkRecordComment(Record savedRecord, Record record) {
        if(!savedRecord.getRecordId().equals(record.getRecordId())) throw new GlobalException(UNMATCHED_RECORD_COMMENT);
    }

    private static boolean checkIsNull(RecordComment recordComment) {
        return recordComment == null;
    }
}
