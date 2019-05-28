package io.atactic.android.cache;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import io.atactic.android.model.Campaign;

public class Cache {

    private final static String FILENAME_CAMPAIGNS = "atactic_campaigns.data";

    public static void save(Context context, List<Campaign> campaignList) throws IOException {
        FileOutputStream fos = context.openFileOutput(FILENAME_CAMPAIGNS, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(campaignList);
        oos.close();
        fos.close();
    }

    public static List<Campaign> loadCampaigns(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(FILENAME_CAMPAIGNS);
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<Campaign> campaigns = (List<Campaign>)ois.readObject();
        return campaigns;
    }

}
