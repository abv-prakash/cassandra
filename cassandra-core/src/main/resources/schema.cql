CREATE TABLE clickevent (
id varchar,
eventRecievedAt varchar,
eventRequestId varchar,
PRIMARY KEY (id,eventRecievedAt)
) WITH comment='Event recorded for given (partitioned on) appId, deviceId and type'
    AND CLUSTERING ORDER BY (eventRecievedAt DESC)
    AND read_repair_chance = 0.1
    AND compression = { 'sstable_compression' : 'SnappyCompressor', 'chunk_length_kb' : 64 }
    AND compaction = { 'class' : 'SizeTieredCompactionStrategy', 'min_threshold' : 6 };