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
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=wDYiXKyExN8&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&initcwndbps=2740000&ms=au&itag=22&mt=1437336576&mv=m&mm=31&mn=sn-85avg-3c2e&expire=1437358243&mime=video%2Fmp4&pl=21&ip=188.230.56.113&key=yt5&lmt=1428904437826943&ratebypass=yes&source=youtube&dur=237.424&id=o-AAgCGN7nCZJW8t07DCv4oKiIpWzW0CZopmINWQDSRhkB&sver=3&fexp=901816%2C9408142%2C9408253%2C9408420%2C9408710%2C9412463%2C9415531%2C9415570%2C9415848%2C9416010%2C9416126%2C9416658%2C9416729%2C9417529&gcr=ua&pcm2cms=yes&ipbits=0&requiressl=yes&signature=88DF53134EFFA049215497B3E90C08DD4A1FEDCD.24A722C433AA88A506E13FB63F3FD35320E16195"));

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdater(getActivity(), mList));

        return rootView;
    }
}
