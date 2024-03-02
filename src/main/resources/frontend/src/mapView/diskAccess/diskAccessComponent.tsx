import React, {useEffect, useState} from "react";
import {DiskAccessManager, start} from "./diskAccessManager";
import {MapManager} from "../../mapmanager";
import {MapSign} from "../../interfaces";

import Styles from "./diskAccessComponent.module.css"

export default function DiskAccessComponent({manager, data}: {
    manager: MapManager,
    data: MapSign[]
}) {
    const [diskAccess, setDiskAccess] = useState<DiskAccessManager | null>(null);
    const [status, setStatus] = useState("");

    useEffect(() => {
        if (diskAccess) diskAccess.updateData(data);
    }, [data])

    if (!window.showDirectoryPicker) return null;

    const chooseFolder = async () => {
        const handle = await window.showDirectoryPicker({
            mode: "readwrite"
        });
        const diskAccess = await start(manager, data, handle, setStatus);
        if (!diskAccess) return;

        setDiskAccess(diskAccess);
    }

    const stopDiskAccess = () => {
        diskAccess?.stop();
        setDiskAccess(null);
    }

    return <div>
        {diskAccess ? <div className={Styles.root}>
            <button onClick={stopDiskAccess}>Stop Watching Disk</button>
            <span>{status}</span>
        </div>:
            <div className={Styles.root}>
                <button onClick={chooseFolder}>Choose Folder</button>
            </div>
        }
    </div>
}