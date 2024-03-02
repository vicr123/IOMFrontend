import Fetch from "./fetch";
import Scrim from "./Scrim";

export class MapManager {
    #onUpdateListener: any;

    setOnUpdateListener(listener: any) {
        this.#onUpdateListener = listener;
    }

    async uploadMaps(maps: File[], category: string) {
        Scrim.mount();
        try {
            for (let file of maps) {
                let imageContents = await this.getImage(file);
                if (!imageContents) continue;

                await Fetch.post("/maps", {
                    name: file.name.replace(/\.[^/.]+$/, ""),
                    category: category,
                    image: imageContents
                });
                await this.#onUpdateListener();
            }
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async deleteMap(id: string) {
        Scrim.mount();
        try {
            await Fetch.delete(`/maps/${id}`);
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async deleteCollection(id: string, collection: string) {
        Scrim.mount();
        try {
            await Fetch.delete(`/maps/${id}/collection/${encodeURIComponent(collection)}`);
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async giveMap(id: string) {
        await Fetch.get(`/maps/${id}/give`)
    }

    async setCatg(id: string, catg: string) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/category`, {
                category: catg
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async addToCollection(id: string, collection: string) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/collection`, {
                name: collection
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async setName(id: string, name: string) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/name`, {
                name: name
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async updateImage(id: number, file: any) {
        Scrim.mount();
        try {
            let imageContents = await this.getImage(file);
            if (!imageContents) return;

            await Fetch.put(`/maps/${id}`, {
                image: imageContents
            });
            await this.#onUpdateListener();
        } catch (e) {
            alert("Unable to upload the image. Check the following:\n- You are connected to the Internet\n- You are replacing a mapsign with the same type of image (i.e. you can't replace a static mapsign with a animated mapsign)\n- You are logged into the Minecraft server\n- You are replacing a new mapsign (you can't replace a mapsign from ImageOnMap)")
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    async createRotondo(id: string) {
        Scrim.mount();
        try {
            await Fetch.post(`/maps/${id}/rotondo`, {

            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }

    getImage(file: any) {
        return new Promise((res, rej) => {
            let reader = new FileReader();
            reader.onload = () => {
                //Perform checks
                let image = new Image();
                image.src = reader.result!.toString();
                image.onload = () => {
                    if (!(image.width % 128 === 0 && image.width % 128 === 0)) {
                        if (!window.confirm("You are uploading an image with a non-canon size. For best results, the image should be sized in multiples of 128x128.")) {
                            rej("Cancelled")
                            return;
                        }
                    }

                    const result = reader.result as string;
                    res(result.substr(result.indexOf(",") + 1));
                }
            }
            reader.onerror = error => rej(error);
            reader.readAsDataURL(file);
        });
    }
}

export default MapManager;