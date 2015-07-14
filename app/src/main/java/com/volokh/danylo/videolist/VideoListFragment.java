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
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?id=o-AIj5aVnWxUFSCFWJyFnmSLNdvLzrP4MnOIWCrnYPEuhN&upn=ysG7efCEB_s&key=yt5&ip=188.230.56.113&ipbits=0&mt=1436731198&mv=m&ms=au&lmt=1431851337967332&mm=31&mn=sn-85avg-3c2e&dur=296.681&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&fexp=901816%2C9407943%2C9407992%2C9408142%2C9408420%2C9408710%2C9409231%2C9412495%2C9412858%2C9415560%2C9415570%2C9416126%2C9416285%2C9416729%2C9416730&itag=22&expire=1436752903&mime=video%2Fmp4&pcm2cms=yes&source=youtube&pl=21&gcr=ua&sver=3&requiressl=yes&initcwndbps=2567500&ratebypass=yes&signature=0590BC1B7D81CCCCBBA1434B58998EBB62D6A65F.AFF90EF0961B1AD805BE3B3D1060D0BA94CE1BDB"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?fexp=901816%2C9408142%2C9408420%2C9408710%2C9415429%2C9415820%2C9416120%2C9416126%2C9416330%2C9417117&expire=1436752858&sver=3&ipbits=0&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&lmt=1422017625033843&requiressl=yes&initcwndbps=2567500&key=yt5&mv=m&pl=21&mt=1436731198&ms=au&id=o-ALULzEcKykCXoQaBKFKczhrVGtE8_7HcDqTlTQCN-ChV&ip=188.230.56.113&ratebypass=yes&mn=sn-85avg-3c2e&source=youtube&mm=31&upn=TKDNfnr8kPc&itag=22&gcr=ua&mime=video%2Fmp4&pcm2cms=yes&dur=301.395&signature=8264EEAA9409423A95BB19F4ACE9B7E160D9A6FF.632697BBF4E78F98B85F8F11E142EA7EDBF73896"));
        mList.add(new YoutubeVideoItem("https://r3---sn-85avg-3c2e.googlevideo.com/videoplayback?source=youtube&sver=3&id=o-ALUAhBtjOjj6UXDT9AqUrBB5Af6iP2g3c6IGyAwce2iI&dur=0.000&mm=31&mn=sn-85avg-3c2e&ms=au&mt=1436730997&mv=m&pcm2cms=yes&ip=188.230.56.113&ratebypass=yes&lmt=1408213916677430&expire=1436752681&fexp=901816%2C923607%2C9405998%2C9406715%2C9407663%2C9407882%2C9408142%2C9408420%2C9408710%2C9413321%2C9414871%2C9414903%2C9415336%2C9416126%2C9416337%2C9416729&gcr=ua&upn=QTsv72UtL7k&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&mime=video%2Fwebm&key=yt5&initcwndbps=2575000&ipbits=0&pl=21&requiressl=yes&itag=43&signature=DADF1BBE6F06A43EA8542BA37E61DF0952B3D1D7.F16354B849FC168BCDFEB89A823B9EB232A172C5"));
        mList.add(new YoutubeVideoItem("https://r2---sn-85avg-3c2e.googlevideo.com/videoplayback?upn=g2BKKilgIqw&ipbits=0&lmt=1428695346003800&expire=1436745917&pcm2cms=yes&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&requiressl=yes&sver=3&mt=1436724249&key=yt5&fexp=901816%2C923612%2C9405998%2C9407662%2C9407942%2C9408142%2C9408195%2C9408420%2C9408710%2C9410705%2C9414764%2C9415414%2C9415662%2C9416126%2C9416328%2C9416600%2C9417347&gcr=ua&ratebypass=yes&itag=22&mn=sn-85avg-3c2e&mm=31&ip=188.230.56.113&mv=m&source=youtube&ms=au&mime=video%2Fmp4&dur=201.734&pl=21&initcwndbps=2673750&id=o-ANCThup1EV5tbID9D_9sLUYKmyEVIE41O8hzgz1JF6Ix&signature=A47C6B4958306B430C626E1DCADD8A1F05493C20.D859FD9464B9F4E720C99EE451493C4C5CCE4B65"));
        mList.add(new YoutubeVideoItem("https://r3---sn-85avg-3c2e.googlevideo.com/videoplayback?ratebypass=yes&mn=sn-85avg-3c2e&mm=31&mv=m&mt=1436730612&ms=au&requiressl=yes&mime=video%2Fmp4&itag=22&pl=21&pcm2cms=yes&fexp=901816%2C9408142%2C9408420%2C9408710%2C9413313%2C9414615%2C9415391%2C9415430%2C9416011%2C9416126%2C9416359&sparams=dur%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&ipbits=0&upn=0m-q2ATB6Q4&signature=88B46A696B5361C7ADF7263F16AA2450277CA999.C7CE3F49D1E512B210948CA7DE9227FB0070D533&sver=3&lmt=1418046022765245&ip=188.230.56.113&key=yt5&expire=1436752307&dur=242.811&initcwndbps=2597500&source=youtube&id=o-ADAT712WBZjHvfwAMU8-Gs3QYJJ9TDweWR1WRVskfKds"));
        mList.add(new YoutubeVideoItem("https://r1---sn-85avg-3c2e.googlevideo.com/videoplayback?ms=au&mt=1436731299&mv=m&dur=280.079&ip=188.230.56.113&initcwndbps=2565000&requiressl=yes&id=o-AP5_jW0iT-HNwy1oEeET1j6Xa7vSFbk9aBTsFG9rQc8D&ipbits=0&mm=31&mn=sn-85avg-3c2e&lmt=1429599273531928&sver=3&upn=MdRhvVvD-TM&expire=1436753003&fexp=901440%2C901816%2C9405988%2C9407053%2C9407662%2C9407967%2C9408142%2C9408420%2C9408710%2C9408939%2C9409261%2C9412840%2C9413004%2C9414762%2C9415295%2C9415931%2C9416126%2C9416331%2C9416729%2C9416847&pcm2cms=yes&source=youtube&mime=video%2Fmp4&pl=21&key=yt5&itag=22&sparams=dur%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&ratebypass=yes&signature=030EFA9693EF7C25FF3EC45804365BE58D89BA1F.4CC8B620D615A94EF81BAC6A943BF18785699051"));
        mList.add(new YoutubeVideoItem("https://r3---sn-85avg-3c2e.googlevideo.com/videoplayback?ratebypass=yes&mn=sn-85avg-3c2e&mm=31&mv=m&mt=1436731395&ms=au&requiressl=yes&gcr=ua&mime=video%2Fmp4&itag=22&pl=21&pcm2cms=yes&fexp=901816%2C9408142%2C9408420%2C9408710%2C9416126%2C966300&sparams=dur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&ipbits=0&upn=bBUDZR5HK18&sver=3&lmt=1429704583510794&ip=188.230.56.113&key=yt5&expire=1436753054&dur=278.755&initcwndbps=2593750&source=youtube&id=o-AF0flZjDRmC7YnaWT6bwVffwWtKleUGiDjVDb16xDV3m&signature=83165FE3D61FF49F9628A7A2711FE6CE21A55255.A95E32372B17B23096C52361193C22DE858942C9"));


        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdater(getActivity(), mList));

        return rootView;
    }
}
