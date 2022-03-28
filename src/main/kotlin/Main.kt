const val COMMISSION_MASTER_MAESTRO_PCT_FACTOR = 60
const val COMMISSION_MASTER_MAESTRO_ADDITION = 20_00
const val COMMISSION_VISA_MIR_PCT_FACTOR = 75
const val COMMISSION_VISA_MIR_ADDITION = 35_00

const val NOTCOMMISSION_MASTER_MAESTRO_MONTH_TRANSFER_SUM_MAX = 75_000_00

const val LIMIT_VKPAY_ONETIME_TRANSFER_SUM_MAX = 15_000_00
const val LIMIT_VKPAY_MONTH_TRANSFER_SUM_MAX = 40_000_00
const val LIMIT_ONE_CARD_DAY_TRANSFER_SUM_MAX = 150_000_00
const val LIMIT_ONE_CARD_MONTH_TRANSFER_SUM_MAX = 600_000_00

enum class CardType {
    Visa, Mir, VkPay, MasterCard, Maestro
}

fun main() {
    print("Введите сумму в копейках, которую вы хотите перевести: ")
    val moneyTransferAmount = readLine()?.toInt() ?: return

    print("Введите общую сумму переводов в копейках за месяц: ")
    val moneyForMonth = readLine()?.toInt() ?: return

    print("Введите одним числом номер соответствующий карте из списка:  0 - Visa; 1 - Mir; 2 - VkPay; 3 - MasterCard; 4 - Maestro: ")
    val cardID = readLine()?.toInt() ?: return

    val cardType: CardType = when (cardID) {
        0 -> CardType.Visa
        1 -> CardType.Mir
        2 -> CardType.VkPay
        3 -> CardType.MasterCard
        4 -> CardType.Maestro
        else -> CardType.VkPay
    }

    val checkLimit = isLimitExceeded(moneyTransferAmount, moneyForMonth, cardType)
    if (checkLimit)
        println("Превышение установленного лимита по карте \"$cardType\"!")
    else {
        val totalCommission = calculateCommission(moneyTransferAmount, moneyForMonth, cardType)
        val totalSum = moneyTransferAmount + totalCommission
        println("Итоговая сумма перевода $moneyTransferAmount коп. по карте \"$cardType\" с комиссией $totalCommission коп. составит: = $totalSum копеек")
    }
}

fun calculateCommission(
    moneyTransferAmount: Int,
    previousSumForMonth: Int = 0,
    cardType: CardType = CardType.VkPay
): Int = when (cardType) {
    CardType.Visa, CardType.Mir -> {
        val calcVisaMirCommission = moneyTransferAmount * COMMISSION_VISA_MIR_PCT_FACTOR / 10_000
        if (calcVisaMirCommission <= COMMISSION_VISA_MIR_ADDITION)
            COMMISSION_VISA_MIR_ADDITION
        else
            calcVisaMirCommission
    }

    CardType.VkPay -> 0

    CardType.MasterCard, CardType.Maestro ->
        if ((moneyTransferAmount + previousSumForMonth) >= NOTCOMMISSION_MASTER_MAESTRO_MONTH_TRANSFER_SUM_MAX)
            (moneyTransferAmount * COMMISSION_MASTER_MAESTRO_PCT_FACTOR / 10_000) + COMMISSION_MASTER_MAESTRO_ADDITION
        else
            0
}

fun isLimitExceeded(
    moneyTransferAmount: Int,
    previousSumForMonth: Int = 0,
    cardType: CardType = CardType.VkPay
): Boolean {
    return if (moneyTransferAmount > LIMIT_ONE_CARD_DAY_TRANSFER_SUM_MAX ||
        (moneyTransferAmount + previousSumForMonth) > LIMIT_ONE_CARD_MONTH_TRANSFER_SUM_MAX
    ) {
        true
    } else {
        if (cardType == CardType.VkPay) {
            if (moneyTransferAmount > LIMIT_VKPAY_ONETIME_TRANSFER_SUM_MAX
                || (moneyTransferAmount + previousSumForMonth) > LIMIT_VKPAY_MONTH_TRANSFER_SUM_MAX
            )
                return true
        }
        false
    }
}