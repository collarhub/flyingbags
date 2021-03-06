package net.flyingbags.flyingapps.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.flyingbags.flyingapps.R;
import net.flyingbags.flyingapps.etc.StateArrayAdapter;
import net.flyingbags.flyingapps.etc.StateListItem;
import net.flyingbags.flyingapps.model.Invoice;
import net.flyingbags.flyingapps.presenter.MainPresenter;
import net.flyingbags.flyingapps.service.MainService;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by User on 2017-10-09.
 */

public class Tab2Fragment extends Fragment implements MainPresenter.view {
    private ListView listView;
    private StateArrayAdapter stateArrayAdapter;
    private ArrayList<StateListItem> arrayList;
    private MainService mainService;
    private ProgressDialog progressDialog;
    private boolean validResultCheck = true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        mainService = new MainService(this);

        arrayList = new ArrayList<>();

        stateArrayAdapter = new StateArrayAdapter(getActivity(), R.layout.item_status, arrayList);

        listView = (ListView) view.findViewById(R.id.list_state);
        listView.setAdapter(stateArrayAdapter);

        mainService.getInvoicesVector();
        progressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressbar_spin);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please check your network environment.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        progressDialog.dismiss();
                                        validResultCheck = false;
                                        onGetInvoicesVectorFailed();
                                    } catch (Exception e) {
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        }, 20000);

        return view;
    }

    @Override
    public void onGetInvoicesVectorFailed() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.footer_my_order, null);
        ((TextView)view.findViewById(R.id.textView_my_order_message)).setText("Data request error.");
        listView.addFooterView(view);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onGetInvoicesVectorSuccess(Vector<String> invoices) {
        if(validResultCheck) {
            for (String invoiceID : invoices) {
                mainService.getInvoice(invoiceID);
            }
            if (invoices.size() == 0) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.footer_my_order, null);
                ((TextView) view.findViewById(R.id.textView_my_order_message)).setText("No data found.");
                listView.addFooterView(view);
            }
        }
        else {
            validResultCheck = true;
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onGetInvoiceSuccess(String invoiceID, Invoice invoice) {
        if(validResultCheck) {
            arrayList.add(new StateListItem(invoiceID, invoice.getOrderDate(), invoice.getRoute(), invoice.getDeparture()));
            stateArrayAdapter.notifyDataSetChanged();
        }
        else {
            validResultCheck = true;
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onGetInvoiceFailed() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.footer_my_order, null);
        ((TextView)view.findViewById(R.id.textView_my_order_message)).setText("Data request error.");
        listView.addFooterView(view);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRegisterInvoiceOnNewOrderFailed() {

    }

    @Override
    public void onRegisterInvoiceOnNewOrderSuccess() {

    }

    @Override
    public void onRegisterInvoiceOnMyListFailed() {

    }

    @Override
    public void onRegisterInvoiceOnMyListSuccess() {

    }

    @Override
    public void onRegisterInvoiceSuccess() {

    }

    @Override
    public void onRegisterInvoiceFailed() {

    }
}
