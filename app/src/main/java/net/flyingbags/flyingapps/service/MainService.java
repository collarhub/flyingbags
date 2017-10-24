package net.flyingbags.flyingapps.service;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.flyingbags.flyingapps.model.Invoice;
import net.flyingbags.flyingapps.model.NewOrder;
import net.flyingbags.flyingapps.presenter.MainPresenter;
import net.flyingbags.flyingapps.view.ScheduleDeliveryActivity;

import java.util.Vector;

public class MainService implements MainPresenter.presenter {
    private static final String TAG = MainService.class.getSimpleName();
    private MainPresenter.view view;

    private AppCompatActivity activity;

    /*@Deprecated
    public MainService(AppCompatActivity activity) {
        this.activity = activity;
        this.view = (MainPresenter.view) activity;
    }*/

    public MainService(MainPresenter.view view) {
        this.view = view;
        this.activity = (AppCompatActivity) view;
    }

    @Override
    public String verifyQR(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() == null)
            return "Scan Failed";
        else {
            activity.startActivity(new Intent(activity, ScheduleDeliveryActivity.class));
            return "Scan Success";
        }
    }

    // make new order by user
    @Override
    public void registerInvoiceOnNewOrder(final String invoice, String location, String target){
        FirebaseDatabase.getInstance().getReference().child("newOrders").child(invoice).setValue(new NewOrder("ready", location, target))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "registerInvoiceOnNewOrder:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            view.onRegisterInvoiceOnNewOrderFailed(); // failed
                        }else{
                            view.onRegisterInvoiceOnNewOrderSuccess(); // success
                        }
                    }
                });
    }

    // order registered on user's list by user
    @Override
    public void registerInvoiceOnMyList(final String invoice, String location, String target){
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(invoice).setValue("ready")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "registerInvoiceOnMyList:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            view.onRegisterInvoiceOnMyListFailed(); // failed
                        }else{
                            view.onRegisterInvoiceOnMyListSuccess(); // success
                        }
                    }
                });
    }

    // get invoices array
    @Override
    public void getInvoicesVector(){
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invoices")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Vector<String> Invoices = new Vector<String>();
                        for(DataSnapshot newOrdersSnapShot : dataSnapshot.getChildren()){
                            Invoices.add(newOrdersSnapShot.getValue(String.class));
                        }
                        view.onGetInvoicesVectorSuccess(Invoices);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        view.onGetInvoicesVectorFailed();
                    }
                });
    }

    // get invoice data
    @Override
    public void getInvoice(final String invoice){
        FirebaseDatabase.getInstance().getReference().child("invoices").child(invoice)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Invoice presentInfo = dataSnapshot.getValue(Invoice.class);
                        if(presentInfo != null) {
                            view.onGetInvoiceSuccess(invoice, presentInfo);
                        }else{
                            view.onGetInvoiceFailed();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        view.onGetInvoiceFailed();
                    }
                });
    }
}
