import React from "react";
import Fetch from "../fetch";
import MapCategory from "./mapCategory";
import MapManager from "../mapmanager";

class MapView extends React.Component {
    constructor(props) {
        super(props);

        this.mapManager = new MapManager();
        this.mapManager.setOnUpdateListener(this.updateData.bind(this));

        this.state = {};
    }

    async componentDidMount() {
        await this.updateData();
    }

    async updateData() {
        try {
            this.setState({
                data: await Fetch.get("/maps")
            })
        } catch (e) {
            this.setState({
                error: "load"
            });
        }
    }

    render() {
        if (this.state.error) {
            return <div>
                Please reload
            </div>
        } else if (this.state.data) {
            return <div>
                {["", ...this.state.data.map(map => map.category ? map.category : "").filter(category => category !== "")].filter((category, index, array) => array.indexOf(category) === index).map(category => <MapCategory manager={this.mapManager} category={category} key={category} data={this.state.data}/>)}
            </div>
        } else {
            return <div>
                Please wait
            </div>
        }
    }
}

export default MapView;