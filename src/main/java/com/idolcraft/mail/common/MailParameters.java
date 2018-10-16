package com.idolcraft.mail.common;

import lombok.Data;
import lombok.NonNull;

@Data
public class MailParameters {
    @NonNull
    private final User user;
}
