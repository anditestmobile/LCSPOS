package id.co.lcs.pos.interfaces;

/**
 * Created by TED on 16-Jul-20
 */

public interface MultipleCallback<A,B> {
    void onCallback(A call_one,B call_two);
}
