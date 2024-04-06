package com.bloxbean.cardano.yaci.core.protocol.chainsync.serializers;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.yaci.core.protocol.Serializer;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.IntersectFound;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Tip;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;

public enum IntersectFoundSerializer implements Serializer<IntersectFound> {
    INSTANCE();


    @Override
    public byte[] serialize(IntersectFound intersectFound) {

        Array array = new Array();
        array.add(new UnsignedInteger(5));

        var point = intersectFound.getPoint();
        array.add(PointSerializer.INSTANCE.serializeDI(point));

        var tip = intersectFound.getTip();
        array.add(TipSerializer.INSTANCE.serializeDI(tip));

        return CborSerializationUtil.serialize(array, false);
    }

    @Override
    public IntersectFound deserialize(byte[] bytes) {
        DataItem di = CborSerializationUtil.deserializeOne(bytes);
        Array array = (Array) di;
        int label = ((UnsignedInteger) array.getDataItems().get(0)).getValue().intValue();
        if (label != 5)
            throw new CborRuntimeException("Invalid label : " + di);

        Point point = PointSerializer.INSTANCE.deserializeDI(array.getDataItems().get(1));
        Tip tip = TipSerializer.INSTANCE.deserializeDI(array.getDataItems().get(2));
        IntersectFound intersectFound = new IntersectFound(point, tip);

        return intersectFound;
    }
}
