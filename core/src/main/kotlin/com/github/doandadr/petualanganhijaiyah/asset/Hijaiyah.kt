package com.github.doandadr.petualanganhijaiyah.asset

enum class Hijaiyah(
    val character: String,
    val id: Int,
    val fileId: String,
    val reading: String,
    val detectionSlack: Int,
    val audio: SoundAsset,
    val audioFathah: SoundAsset? = null,
    val audioKasrah: SoundAsset? = null,
    val audioDhommah: SoundAsset? = null,
    val image: String = "f$fileId-$reading",
    val drawImg: String = "d$fileId-$reading",
    val imageSmall: String = "sf$fileId-$reading",
    val imageNumber: String = "n$fileId-$reading",
) {
    ALIF(
        "أ",
        10,
        "01", "alif", 20,
        SoundAsset.V01_ALIF,
        SoundAsset.V01_ALIF_A,
        SoundAsset.V01_ALIF_I,
        SoundAsset.V01_ALIF_U
    ),
    BA("ب", 11, "02", "ba", 5, SoundAsset.V02_BA, SoundAsset.V02_BA_A, SoundAsset.V02_BA_I, SoundAsset.V02_BA_U),
    TA("ت", 12, "03", "ta", 7, SoundAsset.V03_TA, SoundAsset.V03_TA_A, SoundAsset.V03_TA_I, SoundAsset.V03_TA_U),
    TSA(
        "ث", 13,
        "04", "tsa", 4,
        SoundAsset.V04_TSA,
        SoundAsset.V04_TSA_A,
        SoundAsset.V04_TSA_I,
        SoundAsset.V04_TSA_U
    ),
    JIM(
        "ج", 14,
        "05", "jim", 3,
        SoundAsset.V05_JIM,
        SoundAsset.V05_JIM_A,
        SoundAsset.V05_JIM_I,
        SoundAsset.V05_JIM_U
    ),
    KHA(
        "ح", 15,
        "06", "kha", 5,
        SoundAsset.V06_KHA,
        SoundAsset.V06_KHA_A,
        SoundAsset.V06_KHA_I,
        SoundAsset.V06_KHA_U
    ),
    KHO(
        "خ", 16,
        "07", "kho", 3,
        SoundAsset.V07_KHO,
        SoundAsset.V07_KHO_A,
        SoundAsset.V07_KHO_I,
        SoundAsset.V07_KHO_U
    ),
    DAL(
        "د", 17,
        "08", "dal", 10,
        SoundAsset.V08_DAL,
        SoundAsset.V08_DAL_A,
        SoundAsset.V08_DAL_I,
        SoundAsset.V08_DAL_U
    ),
    DZAL(
        "ذ", 18,
        "09", "dzal", 7,
        SoundAsset.V09_DZAL,
        SoundAsset.V09_DZAL_A,
        SoundAsset.V09_DZAL_I,
        SoundAsset.V09_DZAL_U
    ),
    RA("ر", 19, "10", "ra", 2, SoundAsset.V10_RA, SoundAsset.V10_RA_A, SoundAsset.V10_RA_I, SoundAsset.V10_RA_U),
    ZA("ز", 20, "11", "za", 2, SoundAsset.V11_ZA, SoundAsset.V11_ZA_A, SoundAsset.V11_ZA_I, SoundAsset.V11_ZA_U),
    SIN(
        "س", 21,
        "12", "sin", 6,
        SoundAsset.V12_SIN,
        SoundAsset.V12_SIN_A,
        SoundAsset.V12_SIN_I,
        SoundAsset.V12_SIN_U
    ),
    SYIN(
        "ش", 22,
        "13", "syin", 4,
        SoundAsset.V13_SYIN,
        SoundAsset.V13_SYIN_A,
        SoundAsset.V13_SYIN_I,
        SoundAsset.V13_SYIN_U
    ),
    SHOD(
        "ص", 23,
        "14", "shod", 6,
        SoundAsset.V14_SHOD,
        SoundAsset.V14_SHOD_A,
        SoundAsset.V14_SHOD_I,
        SoundAsset.V14_SHOD_U
    ),
    DHOD(
        "ض", 24,
        "15", "dhod", 5,
        SoundAsset.V15_DHOD,
        SoundAsset.V15_DHOD_A,
        SoundAsset.V15_DHOD_I,
        SoundAsset.V15_DHOD_U
    ),
    THO(
        "ط", 25,
        "16", "tho", 6,
        SoundAsset.V16_THO,
        SoundAsset.V16_THO_A,
        SoundAsset.V16_THO_I,
        SoundAsset.V16_THO_U
    ),
    DHO(
        "ظ", 26,
        "17", "dho", 5,
        SoundAsset.V17_DHO,
        SoundAsset.V17_DHO_A,
        SoundAsset.V17_DHO_I,
        SoundAsset.V17_DHO_U
    ),
    AIN(
        "ع", 27,
        "18", "ain", 10,
        SoundAsset.V18_AIN,
        SoundAsset.V18_AIN_A,
        SoundAsset.V18_AIN_I,
        SoundAsset.V18_AIN_U
    ),
    GHOIN(
        "غ", 28,
        "19", "ghoin", 5,
        SoundAsset.V19_GHOIN,
        SoundAsset.V19_GHOIN_A,
        SoundAsset.V19_GHOIN_I,
        SoundAsset.V19_GHOIN_U
    ),
    FA("ف", 29, "20", "fa", 3, SoundAsset.V20_FA, SoundAsset.V20_FA_A, SoundAsset.V20_FA_I, SoundAsset.V20_FA_U),
    QOF(
        "ق", 30,
        "21", "qof", 2,
        SoundAsset.V21_QOF,
        SoundAsset.V21_QOF_A,
        SoundAsset.V21_QOF_I,
        SoundAsset.V21_QOF_U
    ),
    KAF(
        "ك", 31,
        "22", "kaf", 5,
        SoundAsset.V22_KAF,
        SoundAsset.V22_KAF_A,
        SoundAsset.V22_KAF_I,
        SoundAsset.V22_KAF_U
    ),
    LAM(
        "ل", 32,
        "23", "lam", 8,
        SoundAsset.V23_LAM,
        SoundAsset.V23_LAM_A,
        SoundAsset.V23_LAM_I,
        SoundAsset.V23_LAM_U
    ),
    MIM(
        "م", 33,
        "24", "mim", 10,
        SoundAsset.V24_MIM,
        SoundAsset.V24_MIM_A,
        SoundAsset.V24_MIM_I,
        SoundAsset.V24_MIM_U
    ),
    NUN(
        "ن", 34,
        "25", "nun", 6,
        SoundAsset.V25_NUN,
        SoundAsset.V25_NUN_A,
        SoundAsset.V25_NUN_I,
        SoundAsset.V25_NUN_U
    ),
    WAWU(
        "و", 36,
        "26", "wawu", 5,
        SoundAsset.V26_WAWU,
        SoundAsset.V26_WAWU_A,
        SoundAsset.V26_WAWU_I,
        SoundAsset.V26_WAWU_U
    ),
    HA("ه", 35, "27", "ha", 6, SoundAsset.V27_HA, SoundAsset.V27_HA_A, SoundAsset.V27_HA_I, SoundAsset.V27_HA_U),
    YA("ى", 37, "28", "ya", 3, SoundAsset.V28_YA, SoundAsset.V28_YA_A, SoundAsset.V28_YA_I, SoundAsset.V28_YA_U),
    N00("٠", 0, "100", "sifr", 5, SoundAsset.V100_SIFR),
    N01("١", 1, "101", "wahid", 2, SoundAsset.V101_WAHID),
    N02("٢", 2, "102", "itsnan", 5, SoundAsset.V102_ITSNAN),
    N03("٣", 3, "103", "tsalatsah", 1, SoundAsset.V103_TSALATSAH),
    N04("٤", 4, "104", "arba'ah", 2, SoundAsset.V104_ARBAAH),
    N05("٥", 5, "105", "khamsah", 3, SoundAsset.V105_KHOMSAH),
    N06("٦", 6, "106", "sittah", 2, SoundAsset.V106_SITTAH),
    N07("٧", 7, "107", "sab'ah", 1, SoundAsset.V107_SABAH),
    N08("٨", 8, "108", "tsamaniyah", 1, SoundAsset.V108_TSAMANIYAH),
    N09("٩", 9, "109", "tis'ah", 3, SoundAsset.V109_TISAH),
    N10("١٠", 38, "110", "'asyarah", 1, SoundAsset.V110_ASYARAH),
}
