package com.corelabsplus.el.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.corelabsplus.el.R;
import com.corelabsplus.el.activities.AddEventActivity;
import com.corelabsplus.el.activities.LoginActivity;
import com.corelabsplus.el.activities.MainActivity;
import com.corelabsplus.el.activities.MyEventActivity;
import com.corelabsplus.el.activities.MyEventsActivity;
import com.corelabsplus.el.activities.MyReservationsActivity;
import com.corelabsplus.el.activities.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DrawerUtil {
    static String name, email;

    public static void getDrawer(final Activity activity, Toolbar toolbar, final FirebaseAuth mAuth){

        email = mAuth.getCurrentUser().getEmail();
        name = mAuth.getCurrentUser().getDisplayName();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(
                        new ProfileDrawerItem().withName(name)
                                .withEmail(email)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

//        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
//        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemManageProfile = new PrimaryDrawerItem().withIdentifier(1)
                .withName("Profile")
                .withIcon(R.drawable.ic_account)
                .withIconColor(R.color.white);

        PrimaryDrawerItem drawerItemMyEvents = new PrimaryDrawerItem().withIdentifier(3)
                .withName("My events")
                .withIcon(R.drawable.ic_apps);
        PrimaryDrawerItem drawerItemAddEvent = new PrimaryDrawerItem().withIdentifier(4)
                .withName("Add event")
                .withIcon(R.drawable.ic_add_to_queue);

        PrimaryDrawerItem drawerItemMyReservations = new PrimaryDrawerItem().withIdentifier(2)
                .withName("My Reservations")
                .withIcon(R.drawable.ic_bookmark);
        SecondaryDrawerItem drawerItemLogout = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Logout")
                .withIcon(R.drawable.ic_exit);

        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withAccountHeader(headerResult)
                .withSelectedItem(-1)
                .addDrawerItems(
//                        drawerEmptyItem,drawerEmptyItem,drawerEmptyItem,
                        drawerItemManageProfile,
                        drawerItemMyEvents,
                        drawerItemMyReservations,
                        drawerItemAddEvent,
                        new DividerDrawerItem(),
                        drawerItemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof ProfileActivity)) {
                            // load Add event screen
                            Intent intent = new Intent(activity, ProfileActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier() == 2 && !(activity instanceof MyReservationsActivity)) {
                            // load Add event screen
                            Intent intent = new Intent(activity, MyReservationsActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier() == 3 && !(activity instanceof MyEventsActivity)) {
                            // load Add event screen
                            Intent intent = new Intent(activity, MyEventsActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier() == 4 && !(activity instanceof AddEventActivity)) {
                            // load Add event screen
                            Intent intent = new Intent(activity, AddEventActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier() == 5) {
                            mAuth.signOut();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            view.getContext().startActivity(intent);
                            activity.finish();
                        }
                        return true;
                    }
                })
                .build();
    }
}
