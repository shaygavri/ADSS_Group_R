import java.util.List;

public interface PeriodicOrderRuleDAO {
    boolean add(PeriodicOrderRuleDTO ruleDTO);
    PeriodicOrderRuleDTO findById(String ruleId);
    List<PeriodicOrderRuleDTO> findAll();
    List<PeriodicOrderRuleDTO> findActiveRules();
    boolean updateNextDeliveryDate(String ruleId, String nextDeliveryDate);
    boolean deactivate(String ruleId);
}