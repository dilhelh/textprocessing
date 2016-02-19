package org.kgusarov.textprocessing.analysis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public final class TransliterationServiceFactory {
    private TransliterationServiceFactory() {
    }

    public static TransliterationService create() {
        final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(4);
        poolConfig.setMaxTotal(4);
        poolConfig.setMinIdle(4);

        final TransliterationService transliterationService = new TransliterationService("Any-Lower; [ъь] Remove; " +
                "LvSpecPreProcess; CyrPreProcess; Any-Latin; Latin-ASCII", poolConfig);

        transliterationService.addTransliteratorConfiguration("LvSpecPreProcess", "йи > ji;");
        transliterationService.addTransliteratorConfiguration("CyrPreProcess",
                "йи > ji; ая > aja; оя > oja; эя > eja; ия > ija; уя > uja; ыя > ija; ея > eja; " +
                        "ёя > eja; юя > uja; яя > aja; ай > ai; ой > oi; эй > ei; ий > ii; уй > ui; ый > ii; " +
                        "ей > ei; ёй > ei; юй > ui; яй > ai; ^я > ja; ^е > je; ^ю > ju; ы > i");

        return transliterationService;
    }
}
