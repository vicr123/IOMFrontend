import React, {useEffect, useState} from "react";
import {DiskAccessManager, start} from "./diskAccessManager";
import {MapManager} from "../../mapmanager";
import {MapSign} from "../../interfaces";

import Styles from "./diskAccessComponent.module.css"
import {ControlButton} from "../controlButton";

export default function DiskAccessComponent({manager, data}: {
    manager: MapManager,
    data: MapSign[]
}) {
    const [diskAccess, setDiskAccess] = useState<DiskAccessManager | null>(null);
    const [status, setStatus] = useState("");
    const [diskAccessWarning, setDiskAccessWarning] = useState(false);

    useEffect(() => {
        if (diskAccess) diskAccess.updateData(data);
    }, [data])

    useEffect(() => {
        if (localStorage.getItem("previousDiskAccess")) {
            localStorage.removeItem("previousDiskAccess");
            setDiskAccessWarning(true);
        }
    }, []);

    if (!window.showDirectoryPicker) return null;

    const chooseFolder = async () => {
        const handle = await window.showDirectoryPicker({
            mode: "readwrite"
        });
        const diskAccess = await start(manager, data, handle, setStatus);
        if (!diskAccess) return;

        setDiskAccess(diskAccess);
        localStorage.setItem("previousDiskAccess", "true");
        setDiskAccessWarning(false);

        setStatus("Disk access is currently on.")
    }

    const stopDiskAccess = () => {
        diskAccess?.stop();
        setDiskAccess(null);
        localStorage.removeItem("previousDiskAccess");
    }

    return <div>
        {diskAccess ? <div className={Styles.root}>
                <span className={Styles.header}>Disk Access</span>
                <ControlButton text={"Stop Watching Disk"} onClick={stopDiskAccess} />
                <span>{status}</span>
            </div> :
            <div className={Styles.root}>
                <span className={Styles.header}>Disk Access</span>
                <span>Select a folder to watch for changes. You can save image files to this folder and have IOM automatically upload them.</span>
                <ControlButton text={"Choose Folder"} onClick={chooseFolder} />
            </div>
        }
        {diskAccessWarning && <div className={Styles.diskAccess}>
            <h1>Enable disk access?</h1>
            <p>Last tile you used IOM, disk access was enabled. Do you want to enable disk access now?</p>
            <div>
                <button onClick={() => setDiskAccessWarning(false)}>No</button>
                <button onClick={chooseFolder}>Choose Folder</button>
            </div>
        </div>}
    </div>
}