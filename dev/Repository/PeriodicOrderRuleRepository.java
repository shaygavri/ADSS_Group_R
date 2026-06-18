import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PeriodicOrderRuleRepository {
    private PeriodicOrderRuleDAO periodicOrderRuleDAO;

    public PeriodicOrderRuleRepository(PeriodicOrderRuleDAO periodicOrderRuleDAO) {
        this.periodicOrderRuleDAO = periodicOrderRuleDAO;
    }
    public PeriodicOrderRuleDTO createRule(String productId, int quantity, int dayOfMonth) {
        if (productId == null || productId.isBlank()) {
            return null;}
        if (quantity <= 0) {
            return null;}
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            return null;}
        String ruleId = "POR" + System.currentTimeMillis() + "_" + Math.abs(productId.hashCode());
        LocalDate nextDeliveryDate = calculateNextDeliveryDate(dayOfMonth);
        PeriodicOrderRuleDTO ruleDTO = new PeriodicOrderRuleDTO(ruleId, productId, quantity, dayOfMonth, nextDeliveryDate.toString(), true);
        boolean added = periodicOrderRuleDAO.add(ruleDTO);
        if (!added) {
            return null;}
        return ruleDTO;}

    public PeriodicOrderRuleDTO findById(String ruleId) {
        return periodicOrderRuleDAO.findById(ruleId);}

    public List<PeriodicOrderRuleDTO> findAll() {
        return periodicOrderRuleDAO.findAll();}

    public List<PeriodicOrderRuleDTO> findActiveRules() {
        return periodicOrderRuleDAO.findActiveRules();}

    public List<PeriodicOrderRuleDTO> findRulesThatShouldRunToday() {
        List<PeriodicOrderRuleDTO> rulesToRun = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (PeriodicOrderRuleDTO rule : periodicOrderRuleDAO.findActiveRules()) {
            LocalDate nextDeliveryDate = LocalDate.parse(rule.getNextDeliveryDate());
            LocalDate orderCreationDate = nextDeliveryDate.minusDays(1);
            if (today.equals(orderCreationDate)) {
                rulesToRun.add(rule);
            }
        }

        return rulesToRun;
    }

    public boolean advanceNextDeliveryDate(PeriodicOrderRuleDTO ruleDTO) {
        if (ruleDTO == null) {
            return false;
        }
        LocalDate currentNextDeliveryDate = LocalDate.parse(ruleDTO.getNextDeliveryDate());
        YearMonth nextMonth = YearMonth.from(currentNextDeliveryDate).plusMonths(1);
        LocalDate newNextDeliveryDate = createDateInMonth(nextMonth, ruleDTO.getDayOfMonth());
        return periodicOrderRuleDAO.updateNextDeliveryDate(ruleDTO.getRuleId(), newNextDeliveryDate.toString());
    }

    public boolean deactivate(String ruleId) {
        if (ruleId == null || ruleId.isBlank()) {
            return false;}
        return periodicOrderRuleDAO.deactivate(ruleId);
    }

    private LocalDate calculateNextDeliveryDate(int dayOfMonth) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate candidateDate = createDateInMonth(currentMonth, dayOfMonth);
        if (!candidateDate.isAfter(today)) {
            YearMonth nextMonth = currentMonth.plusMonths(1);
            candidateDate = createDateInMonth(nextMonth, dayOfMonth);
        }
        return candidateDate;
    }

    private LocalDate createDateInMonth(YearMonth yearMonth, int dayOfMonth) {
        int validDay = Math.min(dayOfMonth, yearMonth.lengthOfMonth());
        return yearMonth.atDay(validDay);
    }
}