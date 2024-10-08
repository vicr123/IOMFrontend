import React from "react";
import Styles from "./mapCategory.module.css"
import MapItem from "./mapItem";
import NewMapItemSelector from "./newMapItemSelector";

import Disclosure from "./disclosure.svg";
import Disclosed from "./disclosed.svg";
import {Icon} from "../icon";

class MapCategory extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false,
            collapsed: false
        };
    }

    static categoryName(category) {
        return category || "No Category";
    }

    static categoryLinkName(category) {
        return "cat-" + this.categoryName(category).replace(" ", "-");
    }

    render() {
        return <div className={Styles.CategoryRoot} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)}>
            <div className={Styles.CategoryHeader} onClick={this.toggleCollapse.bind(this)}>
                <a name={MapCategory.categoryLinkName(this.props.category)}/>
                {MapCategory.categoryName(this.props.category)}
                {/*<img className={Styles.Disclosure} src={this.state.collapsed ? Disclosure : Disclosed}/>*/}
                <div style={{flexGrow: "1"}} />
                <Icon icon={this.state.collapsed ? "arrow-down" : "arrow-up"} />
            </div>
            {this.renderMaps()}
        </div>
    }

    renderMaps() {
        if (this.state.collapsed) return null;

        return <div className={[Styles.CategoryItems, this.state.dragging ? Styles.Dragging : ""].join(" ")}>
                {this.props.data.filter(map => map.category ? map.category === this.props.category : !this.props.category).filter(this.props.onFilter).map(map => <MapItem isCollection={false} manager={this.props.manager} key={map.id} data={map} />)}
                <NewMapItemSelector manager={this.props.manager} category={this.props.category} />
        </div>
    }

    toggleCollapse() {
        this.setState((state) => ({
            collapsed: !state.collapsed
        }));
    }

    dragEnter(e) {
        e.preventDefault();
        e.stopPropagation();
        let transfer = e.dataTransfer;
        transfer.dropEffect = "copy";
    }

    dragLeave(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });
    }

    dragOver(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: true
        });
    }

    drop(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });

        let transfer = e.dataTransfer;

        if (transfer.types.includes("application/x.vicr123.iomfrontend.mapid")) {
            if (transfer.getData("application/x.vicr123.iomfrontend.maporigin") === "category") {
                //Moving between categories
                this.props.manager.setCatg(transfer.getData("application/x.vicr123.iomfrontend.mapid"), this.props.category);
            }
        } else {
            //Dropping files
            this.props.manager.uploadMaps(
                [...transfer.items].filter(item => item.kind === "file")
                    .map(item => item.getAsFile())
                    .filter(file => ["image/png", "image/svg+xml", "image/gif", "image/webp"].includes(file.type)), this.props.category);
        }
    }
}

export default MapCategory;