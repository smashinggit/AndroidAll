package com.cs.android.greendao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author ChenSen
 * @date 2021/1/28
 * @desc
 **/

@Entity
public class Good implements Parcelable {

    //@Id 是主键 id，Long 类型，可以通过 @Id(autoincrement = true) 设置自动增长(自动增长主键不能用基本类型 long，只能用包装类型 Long)
    @Id(autoincrement = true)
    private Long id;

    //是向数据库添加了唯一约束
    @Index(unique = true)
    private Long goodsId;

    private String name;
    private String icon;
    private String info;
    private String type;

    protected Good(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            goodsId = null;
        } else {
            goodsId = in.readLong();
        }
        name = in.readString();
        icon = in.readString();
        info = in.readString();
        type = in.readString();
    }

    @Generated(hash = 1771846005)
    public Good(Long id, Long goodsId, String name, String icon, String info, String type) {
        this.id = id;
        this.goodsId = goodsId;
        this.name = name;
        this.icon = icon;
        this.info = info;
        this.type = type;
    }

    @Generated(hash = 2016981037)
    public Good() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (goodsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(goodsId);
        }
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(info);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return this.goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static final Creator<Good> CREATOR = new Creator<Good>() {
        @Override
        public Good createFromParcel(Parcel in) {
            return new Good(in);
        }

        @Override
        public Good[] newArray(int size) {
            return new Good[size];
        }
    };
}
