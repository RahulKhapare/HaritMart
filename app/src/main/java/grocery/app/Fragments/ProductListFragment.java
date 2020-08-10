package grocery.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import grocery.app.R;
import grocery.app.databinding.ActivityProductFragmentBinding;

public class ProductListFragment extends Fragment {

    private ActivityProductFragmentBinding binding;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            context = inflater.getContext();
            binding = DataBindingUtil.inflate(inflater, R.layout.activity_product_fragment, container, false);

        }

        return binding.getRoot();
    }

    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();

        return fragment;
    }
}
