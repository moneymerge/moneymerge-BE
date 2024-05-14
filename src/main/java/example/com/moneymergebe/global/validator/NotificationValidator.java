package example.com.moneymergebe.global.validator;

import static example.com.moneymergebe.global.response.ResultCode.NOT_FOUND_NOTIFICATION;

import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.global.exception.GlobalException;

public class NotificationValidator {
    public static void validate(Notification notification){
        if(checkIsNull(notification)) throw new GlobalException(NOT_FOUND_NOTIFICATION);
    }

    private static boolean checkIsNull(Notification notification) {
        return notification == null;
    }

}
