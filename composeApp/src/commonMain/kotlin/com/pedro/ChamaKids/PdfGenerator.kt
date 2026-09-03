package com.pedro.ChamaKids

import com.pedro.ChamaKids.data.MemberWithStarStats
import com.pedro.ChamaKids.data.MemberWithStats
import com.pedro.ChamaKids.data.PeriodStat

expect object PdfGenerator {
    fun gerarEPrompt(
        mesAno: String,
        membroMaisPresente: MemberWithStats?,
        melhorComportamento: MemberWithStarStats?,
        diaMaiorAssiduidade: PeriodStat?,
        diaMaisEstrelas: PeriodStat?
    )
}
