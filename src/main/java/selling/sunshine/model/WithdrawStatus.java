package selling.sunshine.model;

/**
 * Created by sunshine on 6/6/16.
 * 提现的状态-申请中，已处理
 */
public enum WithdrawStatus {
    APPLY(0), PROCESS(1);

    private int code;

    WithdrawStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
