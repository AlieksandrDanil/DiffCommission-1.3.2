const val COMMISSION_MASTER_MAESTRO_PCT_FACTOR = 60
const val COMMISSION_MASTER_MAESTRO_ADDITION = 20_00
const val COMMISSION_VISA_MIR_PCT_FACTOR = 75
const val COMMISSION_VISA_MIR_ADDITION = 35_00

const val LIMIT_MASTER_MAESTRO_TRANSFER_SUM_MAX = 75_000_00
const val LIMIT_VKPAY_ONETIME_TRANSFER_SUM_MAX = 15_000_00
const val LIMIT_VKPAY_MONTH_TRANSFER_SUM_MAX = 40_000_00
const val LIMIT_ONE_CARD_DAY_TRANSFER_SUM_MAX = 150_000_00
const val LIMIT_ONE_CARD_MONTH_TRANSFER_SUM_MAX = 600_000_00

enum class CardTypes {
    Visa, Mir, VkPay, MasterCard, Maestro
}

fun main() {
    print("Введите сумму в копейках, которую вы хотите перевести: ")
    val moneyTransferAmount = readLine()?.toInt()?: return

    print("Введите общую сумму переводов в копейках за месяц: ")
    val moneyForMonth = readLine()?.toInt()?: return

    print("Введите одним числом номер соответствующий карте из списка:  0 - Visa; 1 - Mir; 2 - VkPay; 3 - MasterCard; 4 - Maestro: ")
    val cardID = readLine()?.toInt()?: return

    val cardType: CardTypes = when(cardID) {
        0 -> CardTypes.Visa
        1 -> CardTypes.Mir
        2 -> CardTypes.VkPay
        3 -> CardTypes.MasterCard
        4 -> CardTypes.Maestro
        else -> CardTypes.VkPay
    }

    val totalCommission = calculateCommission(moneyTransferAmount, moneyForMonth, cardType)
    if (totalCommission == -1) println("Превышение установленного лимита по карте \"$cardType\"!") else

    println("Итоговая сумма перевода $moneyTransferAmount коп. по карте \"$cardType\" с комиссией $totalCommission коп." +
            " составит: = ${moneyTransferAmount + totalCommission} копеек")
}

fun calculateCommission(moneyTransferAmount: Int, previousSumForMonth: Int = 0, cardType: CardTypes = CardTypes.VkPay): Int {
    return if (moneyTransferAmount <= LIMIT_ONE_CARD_DAY_TRANSFER_SUM_MAX && (moneyTransferAmount + previousSumForMonth) <= LIMIT_ONE_CARD_MONTH_TRANSFER_SUM_MAX)
        when (cardType) {
            CardTypes.Visa, CardTypes.Mir -> if (moneyTransferAmount * COMMISSION_VISA_MIR_PCT_FACTOR / 10_000 <= COMMISSION_VISA_MIR_ADDITION)
                COMMISSION_VISA_MIR_ADDITION else (moneyTransferAmount * COMMISSION_VISA_MIR_PCT_FACTOR / 10_000)

            CardTypes.VkPay -> if (moneyTransferAmount <= LIMIT_VKPAY_ONETIME_TRANSFER_SUM_MAX
                && (moneyTransferAmount + previousSumForMonth) <= LIMIT_VKPAY_MONTH_TRANSFER_SUM_MAX) 0 else -1

            CardTypes.MasterCard, CardTypes.Maestro -> if ((moneyTransferAmount + previousSumForMonth) <= LIMIT_MASTER_MAESTRO_TRANSFER_SUM_MAX)
                (moneyTransferAmount * COMMISSION_MASTER_MAESTRO_PCT_FACTOR / 10_000) + COMMISSION_MASTER_MAESTRO_ADDITION else -1
        }
    else
        -1
}