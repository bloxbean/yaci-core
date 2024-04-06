package com.bloxbean.cardano.yaci.core.protocol.chainsync.serializers;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.yaci.core.protocol.Serializer;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.IntersectNotFound;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;

public enum IntersectNotFoundSerializer implements Serializer<IntersectNotFound> {
    INSTANCE();

    @Override
    public byte[] serialize(IntersectNotFound object) {
        Array array = new Array();
        array.add(new UnsignedInteger(6));
        return CborSerializationUtil.serialize(array, false);
    }

    public IntersectNotFound deserialize(byte[] bytes) {
        DataItem di = CborSerializationUtil.deserializeOne(bytes);
        Array array = (Array) di;
        int label = ((UnsignedInteger) array.getDataItems().get(0)).getValue().intValue();
        if (label != 6)
            throw new CborRuntimeException("Invalid label : " + di);

        return new IntersectNotFound(TipSerializer.INSTANCE.deserializeDI(array.getDataItems().get(1)));
    }
}
