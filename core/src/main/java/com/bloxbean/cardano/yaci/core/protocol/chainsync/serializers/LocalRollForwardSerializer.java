package com.bloxbean.cardano.yaci.core.protocol.chainsync.serializers;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.exception.CborRuntimeException;
import com.bloxbean.cardano.yaci.core.common.EraUtil;
import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.core.model.byron.ByronEbBlock;
import com.bloxbean.cardano.yaci.core.model.byron.ByronMainBlock;
import com.bloxbean.cardano.yaci.core.model.serializers.BlockSerializer;
import com.bloxbean.cardano.yaci.core.model.serializers.ByronBlockSerializer;
import com.bloxbean.cardano.yaci.core.model.serializers.ByronEbBlockSerializer;
import com.bloxbean.cardano.yaci.core.protocol.Serializer;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.IntersectNotFound;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.LocalRollForward;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Tip;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;
import com.bloxbean.cardano.yaci.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.bloxbean.cardano.yaci.core.util.CborSerializationUtil.toInt;

@Slf4j
public enum LocalRollForwardSerializer implements Serializer<LocalRollForward> {
    INSTANCE;

    @Override
    public byte[] serialize(LocalRollForward localRollForward) {
        Array array = new Array();
        array.add(new UnsignedInteger(2));


        var blockArray = new Array();
        blockArray.add(new UnsignedInteger(localRollForward.getBlock().getEra().getValue()));

        // TODO: add block serialization

        array.add(TipSerializer.INSTANCE.serializeDI(localRollForward.getTip()));

        return CborSerializationUtil.serialize(array, false);
    }

    public LocalRollForward deserialize(byte[] bytes) {
//        log.info("deserialize: {}", HexUtil.encodeHexString(bytes));
        Array contentArr = (Array)CborSerializationUtil.deserializeOne(bytes);
        List<DataItem> contentDI = contentArr.getDataItems();
        int label = toInt(contentDI.get(0));
        if (label != 2)
            throw new CborRuntimeException("Invalid label : " + contentArr);

        ByteString blockContent = (ByteString) contentDI.get(1);
        byte[] blockBytes = blockContent.getBytes();
        Array blockArray = (Array) CborSerializationUtil.deserializeOne(blockBytes);

        int eraValue = ((UnsignedInteger)blockArray.getDataItems().get(0)).getValue().intValue();
        Era era = EraUtil.getEra(eraValue);

        Block block = null;
        ByronEbBlock byronEbBlock = null;
        ByronMainBlock byronMainBlock = null;
        if (era == Era.Byron) {
            if (eraValue == 0) {
                byronEbBlock = ByronEbBlockSerializer.INSTANCE.deserialize(blockBytes);
            } else {
                byronMainBlock = ByronBlockSerializer.INSTANCE.deserialize(blockBytes);
            }
        } else {
            block = BlockSerializer.INSTANCE.deserialize(blockBytes);
        }

        Tip tip = TipSerializer.INSTANCE.deserializeDI(contentDI.get(2));
        return new LocalRollForward(byronEbBlock, byronMainBlock, block, tip);
    }

}
