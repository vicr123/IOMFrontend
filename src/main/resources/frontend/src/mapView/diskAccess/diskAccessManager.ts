import {MapManager} from "../../mapmanager";
import {MapSign} from "../../interfaces";
import {Dispatch, SetStateAction} from "react";

type StatusStringUpdate =  Dispatch<SetStateAction<string>>;

export class DiskAccessManager {
    private manager: MapManager;
    private handle: FileSystemDirectoryHandle;
    private readonly timeout: ReturnType<typeof setInterval>;
    private oldEntries: {name: string, size: number}[] | undefined;
    private setStatus: StatusStringUpdate;
    private data: MapSign[];
    private polling = false;

    static async create(manager: MapManager, data: MapSign[], handle: FileSystemDirectoryHandle, setStatus: StatusStringUpdate) {
        await handle.getFileHandle(".iomroot", {
            create: true
        });

        return new DiskAccessManager(manager, data, handle, setStatus);
    }

    private constructor(manager: MapManager, data: MapSign[], handle: FileSystemDirectoryHandle, setStatus: StatusStringUpdate) {
        this.manager = manager;
        this.data = data;
        this.handle = handle;
        this.setStatus = setStatus;

        void this.init(data);

        this.timeout = setInterval(() => this.pollFiles(), 1000);
    }

    updateData(data: MapSign[]) {
        this.data = data;
    }

    stop() {
        clearInterval(this.timeout);
    }

    private async init(data: MapSign[]) {
        await this.ensureCategoriesCreated(data);
    }

    private async ensureCategoriesCreated(data: MapSign[]) {
        const categories = [...new Set(data.map(x => x.category).filter(x => x !== ""))];
        for (const category of categories) {
            await this.handle.getDirectoryHandle(category, {
                create: true
            })
        }
    }

    async pollFiles() {
        if (this.polling) return;

        this.polling = true;
        try {
            if (!this.oldEntries) {
                this.oldEntries = await this.entries();
            }

            const newEntries = await this.entries();

            // Calculate a diff
            let addedFiles = newEntries.filter((newEntry) => !this.oldEntries!.some((oldEntry) => oldEntry.name === newEntry.name));
            // let removedFiles = this.oldEntries.filter((oldEntry) => !newEntries.some((newEntry) => newEntry.name === oldEntry.name));
            let changedFiles = newEntries.filter((newEntry) => this.oldEntries!.some((oldEntry) => oldEntry.name === newEntry.name && oldEntry.size !== newEntry.size));

            for (const entry of addedFiles) {
                this.setStatus(`Adding ${entry.name}...`)
                const fileInfo = await this.getFile(entry.name);
                if (!fileInfo) {
                    this.setStatus(`${entry.name} could not be added`)
                    return;
                }

                const [file, fileName, category] = fileInfo;
                await this.manager.uploadMaps([file], category);
                this.setStatus(`Added ${entry.name}.`)
            }

            for (const entry of changedFiles) {
                this.setStatus(`Updating ${entry.name}...`);
                const fileInfo = await this.getFile(entry.name);
                if (!fileInfo) {
                    this.setStatus(`${entry.name} could not be updated`)
                    return;
                }
                const [file, fileName, category] = fileInfo

                const id = this.data.filter(x => x.category == category && x.name == fileName.substring(0, fileName.lastIndexOf(".")))[0].id;

                await this.manager.updateImage(id, file);
                this.setStatus(`Updated ${entry.name}.`);
            }

            this.oldEntries = newEntries;
        } finally {
            this.polling = false;
        }
    }

    async getFile(name: string): Promise<[File, string, string] | null> {
        try {
            if (name.includes("/")) {
                const [category, fileName] = name.split("/");
                const dirHandle = await this.handle.getDirectoryHandle(category);
                const fileHandle = await dirHandle.getFileHandle(fileName);
                return [await fileHandle.getFile(), fileName, category]
            } else {
                const fileHandle = await this.handle.getFileHandle(name);
                return [await fileHandle.getFile(), name, ""];
            }
        } catch {
            return null;
        }
    }

    async entries() {
        let entries = [];
        for await (const [key, value] of this.handle.entries()) {
            if (value.kind == "file") {
                if (!this.validFileName(key)) continue;
                const file = await value.getFile();
                entries.push({
                    name: key,
                    size: file.size
                })
            } else {
                // Iterate through this directory
                for await (const [innerKey, innerValue] of value.entries()) {
                    if (innerValue.kind == "file") {
                        if (!this.validFileName(innerKey)) continue;
                        const file = await innerValue.getFile();
                        entries.push({
                            name: `${key}/${innerKey}`,
                            size: file.size
                        });
                    }
                }
            }
        }

        return entries;
    }

    private validFileName(key: string) {
        return /(\.svg|\.png|\.gif|\.webp)$/.test(key)
    }
}

export async function start(manager: MapManager, data: MapSign[], handle: FileSystemDirectoryHandle, setStatus: StatusStringUpdate) {
    try {
        await handle.getFileHandle(".iomroot", {
            create: false
        });
    } catch (e) {
        // .iomroot doesn't exist

        // noinspection LoopStatementThatDoesntLoopJS
        for await (const [key, value] of handle.entries()) {
            alert("Please select an empty directory, or a directory containing IOM data.")
            return null;
        }
    }

    return DiskAccessManager.create(manager, data, handle, setStatus);
}
