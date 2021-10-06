import Fetch from "./fetch";
import Scrim from "./Scrim";

class MapManager {
    #onUpdateListener;

    setOnUpdateListener(listener) {
        this.#onUpdateListener = listener;
    }

    async uploadMaps(maps, category) {
        Scrim.mount();
        try {
            for (let file of maps) {
                let imageContents = await new Promise((res, rej) => {
                    let reader = new FileReader();
                    reader.onload = () => res(reader.result.substr(22));
                    reader.onerror = error => rej(error);
                    reader.readAsDataURL(file);
                });

                await Fetch.post("/maps", {
                    name: file.name.replace(".png", ""),
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

    async deleteMap(id) {
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

    async giveMap(id) {
        await Fetch.get(`/maps/${id}/give`)
    }

    async setCatg(id, catg) {
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

    async setName(id, name) {
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

    async updateImage(id, file) {
        Scrim.mount();
        try {
            let imageContents = await new Promise((res, rej) => {
                let reader = new FileReader();
                reader.onload = () => res(reader.result.substr(22));
                reader.onerror = error => rej(error);
                reader.readAsDataURL(file);
            });

            await Fetch.put(`/maps/${id}`, {
                image: imageContents
            });
            await this.#onUpdateListener();
        } catch (e) {
            throw e;
        } finally {
            Scrim.unmount();
        }
    }
}

export default MapManager;