package com.volokh.danylo.videolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.volokh.danylo.videolist.adapter.VideoItem;
import com.volokh.danylo.videolist.adapter.VideoListAdater;
import com.volokh.danylo.videolist.adapter.YoutubeVideoItem;

import java.util.ArrayList;

public class VideoListFragment extends Fragment{

    private ListView mListView;
    private final ArrayList<VideoItem> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=g2BKKilgIqw&ipbits=0&lmt=1428695346003800&expire=1436745917&pcm2cms=yes&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&requiressl=yes&sver=3&mt=1436724249&key=yt5&fexp=901816%2C923612%2C9405998%2C9407662%2C9407942%2C9408142%2C9408195%2C9408420%2C9408710%2C9410705%2C9414764%2C9415414%2C9415662%2C9416126%2C9416328%2C9416600%2C9417347&gcr=ua&ratebypass=yes&itag=22&mn=sn-85avg-3c2e&mm=31&ip=188.230.56.113&mv=m&source=youtube&ms=au&mime=video%2Fmp4&dur=201.734&pl=21&initcwndbps=2673750&id=o-ANCThup1EV5tbID9D_9sLUYKmyEVIE41O8hzgz1JF6Ix&signature=A47C6B4958306B430C626E1DCADD8A1F05493C20.D859FD9464B9F4E720C99EE451493C4C5CCE4B65"));

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdater(getActivity(), mList));

        return rootView;
    }
}
