package com.bloxbean.cardano.yaci.core.protocol.chainsync.serializers;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.yaci.core.protocol.Serializer;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.FindIntersect;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;

public enum FindIntersectSerializer implements Serializer<FindIntersect> {
    INSTANCE();


    @Override
    public byte[] serialize(FindIntersect intersect) {
        Array array = new Array();
        array.add(new UnsignedInteger(4));
        Array pointsArr = new Array();
        if (intersect.getPoints() != null) {
            for (Point point : intersect.getPoints()) {
                pointsArr.add(PointSerializer.INSTANCE.serializeDI(point));
            }
        }
        array.add(pointsArr);

        return CborSerializationUtil.serialize(array, false);
    }

    @Override
    public FindIntersect deserialize(byte[] bytes) {

        DataItem di = CborSerializationUtil.deserializeOne(bytes);
        Array array = (Array) di;

        int label = ((UnsignedInteger) array.getDataItems().get(0)).getValue().intValue();
        if (label != 1)
            throw new CborRuntimeException("Invalid label : " + di);

        var pointsArr = (Array) array.getDataItems().get(1);
        var points = pointsArr.getDataItems()
                .stream()
                .map(PointSerializer.INSTANCE::deserializeDI)
                .toArray(Point[]::new);

        return new FindIntersect(points);

    }

}
