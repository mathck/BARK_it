package com.barkitapp.android.prime;

import com.barkitapp.android.core.utility.PostComperator;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.objects.Post;

import java.util.Collections;
import java.util.List;

public class NewFragment extends PostFragment {

    @Override
    public void sort(List<Post> masterList) {
        Collections.sort(masterList, new PostComperator.Time());
    }

    @Override
    public Order getOrder() {
        return Order.TIME;
    }
}
