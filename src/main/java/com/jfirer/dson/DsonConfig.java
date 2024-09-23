package com.jfirer.dson;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DsonConfig
{
    public static final DsonConfig STANDARD       = new DsonConfig();
    private             boolean    readUseCompile = false;
}
