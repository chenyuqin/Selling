package selling.sunshine.model;

/**
 * Created by sunshine on 5/17/16.
 */
public class RefundConfig extends Entity {
    private String refundConfigId;
    private Goods goods;
    private int amountTrigger;
    private double percent;

    public RefundConfig() {
        super();
    }

    public RefundConfig(Goods goods, int amountTrigger, double percent) {
        this();
        this.goods = goods;
        this.amountTrigger = amountTrigger;
        this.percent = percent;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getRefundConfigId() {
        return refundConfigId;
    }

    public void setRefundConfigId(String refundConfigId) {
        this.refundConfigId = refundConfigId;
    }

    public int getAmountTrigger() {
        return amountTrigger;
    }

    public void setAmountTrigger(int amountTrigger) {
        this.amountTrigger = amountTrigger;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}