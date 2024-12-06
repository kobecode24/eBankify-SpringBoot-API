package org.system.bank.otp;

import org.system.bank.enums.OtpPurpose;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresOtp {
    OtpPurpose purpose();
}
